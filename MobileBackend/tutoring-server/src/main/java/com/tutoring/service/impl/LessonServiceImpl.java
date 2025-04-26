package com.tutoring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutoring.dao.CourseDao;
import com.tutoring.dao.CourseRegistrationDao;
import com.tutoring.dao.LessonDao;
import com.tutoring.dao.UserDao;
import com.tutoring.dto.CreateLessonRequest;
import com.tutoring.entity.Course;
import com.tutoring.entity.CourseRegistration;
import com.tutoring.entity.Lesson;
import com.tutoring.entity.User;
import com.tutoring.enumeration.ErrorCode;
import com.tutoring.exception.CustomException;
import com.tutoring.service.LessonService;
import com.tutoring.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LessonServiceImpl extends ServiceImpl<LessonDao, Lesson> implements LessonService {

    @Autowired private CourseDao             courseDao;
    @Autowired private CourseRegistrationDao registrationDao;
    @Autowired private UserDao userDao;          // 直接查邮箱即可
    @Autowired private MailService mailService;

    @Override
    @Transactional
    public Lesson createLesson(Long tutorId,
                               CreateLessonRequest request) {

        /* ---------- 1. 基础校验 ---------- */
        Course course = courseDao.selectById(request.getCourseId());
        if (course == null)
            throw new CustomException(ErrorCode.NOT_FOUND, "Course not found.");
        if (!course.getTutorId().equals(tutorId))
            throw new CustomException(ErrorCode.FORBIDDEN,
                    "You are not the owner of this course.");

        /* ---------- 2. 保存 Lesson ---------- */
        Lesson lesson = Lesson.builder()
                .courseId(request.getCourseId())
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrls(request.getImageUrls())
                .pdfUrls(request.getPdfUrls())
                .isCompleted(false)
                .build();
        save(lesson);
        log.info("Lesson[{}] created for course[{}]", lesson.getLessonId(), course.getCourseId());

        /* ---------- 3. 取所有已选课学生 ---------- */
        List<CourseRegistration> regs = registrationDao.selectList(
                new LambdaQueryWrapper<CourseRegistration>()
                        .eq(CourseRegistration::getCourseId, course.getCourseId())
                        .eq(CourseRegistration::getStatus,
                                CourseRegistration.RegistrationStatus.approved)
        );
        if (regs.isEmpty()) return lesson;                    // 无学生，结束

        Set<Long> studentIds = regs.stream()
                .map(CourseRegistration::getStudentId)
                .collect(Collectors.toSet());
        // 仅取 email，避免加载无用列
        List<User> students = userDao.selectBatchIds(studentIds);
        String courseName = course.getCourseName();
        String lessonTitle = lesson.getTitle();

        /* ---------- 4. 异步群发邮件 ---------- */
        students.stream()
                .map(User::getEmail)
                .filter(StringUtils::hasText)
                .forEach(email ->
                        mailService.sendLessonNotification(email, courseName, lessonTitle));

        return lesson;
    }
}