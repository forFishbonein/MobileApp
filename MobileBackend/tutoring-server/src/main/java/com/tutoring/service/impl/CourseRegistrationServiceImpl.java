package com.tutoring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutoring.dao.CourseDao;
import com.tutoring.dao.CourseRegistrationDao;
import com.tutoring.dao.LessonProgressDao;
import com.tutoring.entity.Course;
import com.tutoring.entity.CourseRegistration;
import com.tutoring.entity.Lesson;
import com.tutoring.entity.LessonProgress;
import com.tutoring.enumeration.ErrorCode;
import com.tutoring.exception.CustomException;
import com.tutoring.service.CourseRegistrationService;
import com.tutoring.service.LessonService;
import com.tutoring.vo.CourseProgressResponse;
import com.tutoring.vo.LessonProgressItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CourseRegistrationServiceImpl extends ServiceImpl<CourseRegistrationDao, CourseRegistration>
        implements CourseRegistrationService {

    @Autowired
    private LessonService lessonService; // 用于查询课程下所有 Lesson

    @Autowired
    private LessonProgressDao lessonProgressDao; // 用于查询学生在 Lesson 上的进度

    @Autowired
    private CourseDao courseDao; // 用于验证课程归属（审批时需确保当前导师拥有该课程）

    @Override
    public void registerCourse(Long studentId, Long courseId) {
        CourseRegistration registration = CourseRegistration.builder()
                .courseId(courseId)
                .studentId(studentId)
                .status(CourseRegistration.RegistrationStatus.pending)
                .build();
        this.baseMapper.insert(registration);
        log.info("Student {} registered for course {}", studentId, courseId);
    }

    @Override
    public List<CourseRegistration> findRegistrationsByTutor(Long tutorId) {
        // 查询当前 tutor 所有课程
        QueryWrapper<Course> courseQuery = new QueryWrapper<>();
        courseQuery.eq("tutor_id", tutorId);
        List<Course> tutorCourses = courseDao.selectList(courseQuery);
        if (tutorCourses == null || tutorCourses.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> courseIds = new ArrayList<>();
        for (Course course : tutorCourses) {
            courseIds.add(course.getCourseId());
        }
        // 查询状态为 pending 的注册请求，且 course_id 属于 tutorCourses

        // 改成查出所有状态的注册请求
        QueryWrapper<CourseRegistration> regQuery = new QueryWrapper<>();
        regQuery.in("course_id", courseIds);
//                .eq("status", CourseRegistration.RegistrationStatus.pending);
        return this.baseMapper.selectList(regQuery);
    }

    @Override
    public void updateRegistrationStatus(Long registrationId, String decision, Long tutorId) {
        // 先获取注册记录
        CourseRegistration registration = this.baseMapper.selectById(registrationId);
        if (registration == null) {
            throw new CustomException(ErrorCode.NOT_FOUND, "Registration record not found.");
        }
        // 验证该注册请求所属课程是否属于当前 tutor
        Course course = courseDao.selectById(registration.getCourseId());
        if (course == null || !course.getTutorId().equals(tutorId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "You are not authorized to update this registration.");
        }
        // 根据 decision 更新状态
        if ("approved".equalsIgnoreCase(decision)) {
            registration.setStatus(CourseRegistration.RegistrationStatus.approved);
        } else if ("rejected".equalsIgnoreCase(decision)) {
            registration.setStatus(CourseRegistration.RegistrationStatus.rejected);
        } else {
            throw new CustomException(ErrorCode.BAD_REQUEST, "Invalid decision value.");
        }
        this.baseMapper.updateById(registration);
        log.info("Registration {} updated to {} by tutor {}", registrationId, registration.getStatus(), tutorId);
    }

    @Override
    public CourseProgressResponse getCourseProgress(Long studentId, Long courseId) {
        // 查询该课程下所有 Lesson
        QueryWrapper<Lesson> lessonQuery = new QueryWrapper<>();
        lessonQuery.eq("course_id", courseId);
        List<Lesson> lessons = lessonService.list(lessonQuery);

        List<LessonProgressItem> progressItems = new ArrayList<>();
        for (Lesson lesson : lessons) {
            QueryWrapper<LessonProgress> progressQuery = new QueryWrapper<>();
            progressQuery.eq("lesson_id", lesson.getLessonId())
                    .eq("student_id", studentId);
            LessonProgress progress = lessonProgressDao.selectOne(progressQuery);

            LessonProgressItem item = new LessonProgressItem();
            item.setLessonId(lesson.getLessonId());
            item.setLessonTitle(lesson.getTitle());
            if (progress == null) {
                item.setProgress(LessonProgress.ProgressStatus.not_started.name());
            } else {
                item.setProgress(progress.getStatus().name());
            }
            progressItems.add(item);
        }
        CourseProgressResponse response = new CourseProgressResponse();
        response.setCourseId(courseId);
        response.setLessonProgressList(progressItems);
        return response;
    }

    @Override
    public List<CourseRegistration> findRegistrationsByStudent(Long studentId) {
        QueryWrapper<CourseRegistration> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        return this.baseMapper.selectList(queryWrapper);
    }
}
