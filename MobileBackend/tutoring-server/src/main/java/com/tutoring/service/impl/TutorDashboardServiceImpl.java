package com.tutoring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tutoring.dao.*;
import com.tutoring.entity.*;
import com.tutoring.enumeration.ErrorCode;
import com.tutoring.exception.CustomException;
import com.tutoring.service.TutorDashboardService;
import com.tutoring.vo.LessonProgressItem;
import com.tutoring.vo.StudentProgressVO;
import com.tutoring.vo.TutorDashboardResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TutorDashboardServiceImpl implements TutorDashboardService {

    @Autowired private CourseDao             courseDao;
    @Autowired private CourseRegistrationDao regDao;
    @Autowired private LessonDao             lessonDao;
    @Autowired private LessonProgressDao     progressDao;
    @Autowired private UserDao               userDao;

    @Override
    public TutorDashboardResponse getDashboardData(Long tutorId, Long courseId) {
        // 1. 验证课程存在且归属
        Course course = courseDao.selectById(courseId);
        if (course == null) {
            throw new CustomException(ErrorCode.NOT_FOUND, "Course not found.");
        }
        if (!course.getTutorId().equals(tutorId)) {
            throw new CustomException(ErrorCode.FORBIDDEN,
                    "You are not the owner of this course.");
        }

        // 2. 统计总课时（避免除零）
        int rawCount = lessonDao.selectCount(
                new LambdaQueryWrapper<Lesson>()
                        .eq(Lesson::getCourseId, courseId)
        );
        final int totalLessons = rawCount == 0 ? 1 : rawCount;


        // 3. 查已批准注册学生
        List<CourseRegistration> regs = regDao.selectList(
                new LambdaQueryWrapper<CourseRegistration>()
                        .eq(CourseRegistration::getCourseId, courseId)
                        .eq(CourseRegistration::getStatus,
                                CourseRegistration.RegistrationStatus.approved)
        );

        // 无学生直接返回空结构
        if (regs.isEmpty()) {
            return TutorDashboardResponse.builder()
                    .courseId(courseId)
                    .courseName(course.getCourseName())
                    .studentCount(0)
                    .students(Collections.emptyList())
                    .build();
        }

        // 4. 批量拉昵称
        List<Long> studentIds = regs.stream()
                .map(CourseRegistration::getStudentId)
                .collect(Collectors.toList());
        Map<Long,String> nickMap = userDao.selectBatchIds(studentIds).stream()
                .collect(Collectors.toMap(User::getUserId, User::getNickname));

        // 5. 查询已完成课时数：按 student_id 分组，COUNT(*) AS completed
        QueryWrapper<LessonProgress> countWrapper = new QueryWrapper<>();
        countWrapper.select("student_id", "COUNT(*) AS completed")
                .eq("course_id", courseId)
                .eq("status", LessonProgress.ProgressStatus.completed.name())
                .groupBy("student_id");

        List<Map<String, Object>> rows = progressDao.selectMaps(countWrapper);
        Map<Long, Integer> doneMap = rows.stream().collect(Collectors.toMap(
                r -> ((Number) r.get("student_id")).longValue(),
                r -> ((Number) r.get("completed")).intValue()
        ));

        // 6. 构造并按完成度降序排序学生进度列表
        List<StudentProgressVO> students = studentIds.stream()
                .map(sid -> {
                    int done = doneMap.getOrDefault(sid, 0);
                    int pct  = (int) Math.round(done * 100.0 / totalLessons);
                    return StudentProgressVO.builder()
                            .studentId(sid)
                            .nickname(nickMap.getOrDefault(sid, "Unknown"))
                            .progressPercent(pct)
                            .build();
                })
                // 用 comparingInt 消除泛型警告
                .sorted(Comparator.comparingInt(StudentProgressVO::getProgressPercent).reversed())
                .collect(Collectors.toList());

        return TutorDashboardResponse.builder()
                .courseId(courseId)
                .courseName(course.getCourseName())
                .studentCount(students.size())
                .students(students)
                .build();
    }

    @Override
    public List<TutorDashboardResponse> getAllDashboardData(Long tutorId) {
        // 查询该 tutor 的所有课程
        List<Course> courses = courseDao.selectList(
                new LambdaQueryWrapper<Course>()
                        .eq(Course::getTutorId, tutorId)
        );
        // 对每门课生成 Dashboard
        return courses.stream()
                .map(c -> getDashboardData(tutorId, c.getCourseId()))
                .collect(Collectors.toList());
    }
}
