package com.tutoring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tutoring.dao.*;
import com.tutoring.entity.*;
import com.tutoring.service.TutorDashboardService;
import com.tutoring.vo.TutorDashboardResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TutorDashboardServiceImpl implements TutorDashboardService {

    @Autowired
    private CourseDao courseDao; // 查询课程

    @Autowired
    private CourseRegistrationDao courseRegistrationDao; // 查询注册记录

    @Autowired
    private LessonDao lessonDao; // 查询课程下的 Lesson

    @Autowired
    private LessonProgressDao lessonProgressDao; // 查询学生在 Lesson 上的进度

    @Autowired
    private UserDao userDao; // 查询学生信息

    @Override
    public TutorDashboardResponse getDashboardData(Long tutorId) {
        TutorDashboardResponse response = new TutorDashboardResponse();
        List<TutorDashboardResponse.CourseDashboardItem> courseItems = new ArrayList<>();

        // 查询当前导师所有课程
        QueryWrapper<Course> courseQuery = new QueryWrapper<>();
        courseQuery.eq("tutor_id", tutorId);
        List<Course> courses = courseDao.selectList(courseQuery);
        if (courses != null) {
            for (Course course : courses) {
                TutorDashboardResponse.CourseDashboardItem courseItem = new TutorDashboardResponse.CourseDashboardItem();
                courseItem.setCourseId(course.getCourseId());
                courseItem.setCourseName(course.getCourseName());

                // 查询审批通过的注册记录（状态为 approved）
                QueryWrapper<CourseRegistration> regQuery = new QueryWrapper<>();
                regQuery.eq("course_id", course.getCourseId())
                        .eq("status", CourseRegistration.RegistrationStatus.approved);
                List<CourseRegistration> registrations = courseRegistrationDao.selectList(regQuery);
                int regCount = registrations != null ? registrations.size() : 0;
                courseItem.setRegistrationCount(regCount);

                List<TutorDashboardResponse.StudentProgress> studentProgressList = new ArrayList<>();
                // 对于每个注册学生，查询他们在该课程中各 Lesson 的进度
                if (registrations != null && !registrations.isEmpty()) {
                    // 查询当前课程的所有 Lesson
                    QueryWrapper<Lesson> lessonQuery = new QueryWrapper<>();
                    lessonQuery.eq("course_id", course.getCourseId());
                    List<Lesson> lessons = lessonDao.selectList(lessonQuery);

                    for (CourseRegistration reg : registrations) {
                        TutorDashboardResponse.StudentProgress studentProgress = new TutorDashboardResponse.StudentProgress();
                        studentProgress.setStudentId(reg.getStudentId());
                        // 获取学生昵称
                        User student = userDao.selectById(reg.getStudentId());
                        studentProgress.setStudentNickname(student != null ? student.getNickname() : "");

                        List<TutorDashboardResponse.LessonProgressItem> lessonProgressItems = new ArrayList<>();
                        if (lessons != null) {
                            for (Lesson lesson : lessons) {
                                TutorDashboardResponse.LessonProgressItem lpItem = new TutorDashboardResponse.LessonProgressItem();
                                lpItem.setLessonId(lesson.getLessonId());
                                lpItem.setLessonTitle(lesson.getTitle());
                                // 查询当前学生在该 Lesson 的进度
                                QueryWrapper<LessonProgress> progressQuery = new QueryWrapper<>();
                                progressQuery.eq("student_id", reg.getStudentId())
                                        .eq("lesson_id", lesson.getLessonId());
                                LessonProgress progress = lessonProgressDao.selectOne(progressQuery);
                                if (progress == null) {
                                    lpItem.setProgress(LessonProgress.ProgressStatus.not_started.name());
                                } else {
                                    lpItem.setProgress(progress.getStatus().name());
                                }
                                lessonProgressItems.add(lpItem);
                            }
                        }
                        studentProgress.setLessonProgressItems(lessonProgressItems);
                        studentProgressList.add(studentProgress);
                    }
                }
                courseItem.setStudentProgressList(studentProgressList);
                courseItems.add(courseItem);
            }
        }
        response.setCourses(courseItems);
        return response;
    }
}
