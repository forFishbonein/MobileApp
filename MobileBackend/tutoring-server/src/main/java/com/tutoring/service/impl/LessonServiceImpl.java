package com.tutoring.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutoring.dao.CourseDao;
import com.tutoring.dao.LessonDao;
import com.tutoring.dto.CreateLessonRequest;
import com.tutoring.entity.Course;
import com.tutoring.entity.Lesson;
import com.tutoring.enumeration.ErrorCode;
import com.tutoring.exception.CustomException;
import com.tutoring.service.LessonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LessonServiceImpl extends ServiceImpl<LessonDao, Lesson> implements LessonService {

    @Autowired
    private CourseDao courseDao; // 用来查询 Course

    @Override
    public Lesson createLesson(Long tutorId, CreateLessonRequest request) {
        // 1. 验证 Course 是否存在
        Course course = courseDao.selectById(request.getCourseId());
        if (course == null) {
            throw new CustomException(ErrorCode.NOT_FOUND, "Course not found.");
        }

        // 2. 验证是否是该课程的拥有者
        if (!course.getTutorId().equals(tutorId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "You are not the owner of this course.");
        }

        // 3. 构造 Lesson 实体，直接使用传入的字符串
        Lesson lesson = new Lesson();
        lesson.setCourseId(request.getCourseId());
        lesson.setTitle(request.getTitle());
        lesson.setContent(request.getContent());
        lesson.setImageUrls(request.getImageUrls()); // 图片链接逗号分隔的字符串
        lesson.setPdfUrls(request.getPdfUrls());     // PDF链接逗号分隔的字符串

        // 4. 保存到数据库
        this.save(lesson);
        log.info("New lesson created: lessonId={}, courseId={}", lesson.getLessonId(), lesson.getCourseId());
        return lesson;
    }

    @Override
    public void completeLesson(Long lessonId, Long tutorId) {
        // 1. 查询 Lesson 是否存在
        Lesson lesson = this.getById(lessonId);
        if (lesson == null) {
            throw new CustomException(ErrorCode.NOT_FOUND, "Lesson not found.");
        }
        // 2. 查询对应的 Course
        Course course = courseDao.selectById(lesson.getCourseId());
        if (course == null) {
            throw new CustomException(ErrorCode.NOT_FOUND, "Course not found.");
        }
        // 3. 验证当前导师是否为该课程的拥有者
        if (!course.getTutorId().equals(tutorId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "You are not the owner of this course.");
        }
        // 4. 更新 Lesson 的状态为已完成
        lesson.setCompleted(true);
        this.updateById(lesson);
    }
}