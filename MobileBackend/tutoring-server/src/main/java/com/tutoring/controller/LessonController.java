package com.tutoring.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tutoring.dao.CourseDao;
import com.tutoring.dto.CreateLessonRequest;
import com.tutoring.dto.UpdateLessonRequest;
import com.tutoring.entity.Course;
import com.tutoring.entity.Lesson;
import com.tutoring.entity.User;
import com.tutoring.enumeration.ErrorCode;
import com.tutoring.exception.CustomException;
import com.tutoring.result.RestResult;
import com.tutoring.service.LessonService;
import com.tutoring.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/lesson")
@Slf4j
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @Autowired
    private CourseDao courseDao; // 用于验证课程归属

    /**
     * POST /lesson/create
     * 导师创建 Lesson
     */
    @PostMapping("/create")
    public RestResult<Lesson> createLesson(@Valid @RequestBody CreateLessonRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "User is not authenticated.");
        }
        if (SecurityUtils.getCurrentUserRole() != User.Role.tutor) {
            throw new CustomException(ErrorCode.FORBIDDEN, "Only tutor can create lessons.");
        }
        Lesson newLesson = lessonService.createLesson(currentUserId, request);
        return RestResult.success(newLesson, "Lesson created successfully.");
    }

    /**
     * PUT /lesson/{lessonId}
     * 导师更新 Lesson
     */
    @PutMapping("/{lessonId}")
    public RestResult<Lesson> updateLesson(@PathVariable Long lessonId,
                                           @Valid @RequestBody UpdateLessonRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "User is not authenticated.");
        }
        if (SecurityUtils.getCurrentUserRole() != User.Role.tutor) {
            throw new CustomException(ErrorCode.FORBIDDEN, "Only tutor can update lessons.");
        }
        Lesson lesson = lessonService.getById(lessonId);
        if (lesson == null) {
            throw new CustomException(ErrorCode.NOT_FOUND, "Lesson not found.");
        }
        // 验证该 Lesson 是否属于当前 tutor 的课程
        Course course = courseDao.selectById(lesson.getCourseId());
        if (course == null || !course.getTutorId().equals(currentUserId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "You are not authorized to update this lesson.");
        }
        lesson.setTitle(request.getTitle());
        lesson.setContent(request.getContent());
        lesson.setImageUrls(request.getImageUrls());
        lesson.setPdfUrls(request.getPdfUrls());
        lesson.setCompleted(request.getIsCompleted());
        lessonService.updateById(lesson);
        return RestResult.success(lesson, "Lesson updated successfully.");
    }

    /**
     * GET /lesson/{lessonId}
     * 获取指定 Lesson 详情
     */
    @GetMapping("/{lessonId}")
    public RestResult<Lesson> getLesson(@PathVariable Long lessonId) {
        Lesson lesson = lessonService.getById(lessonId);
        if (lesson == null) {
            throw new CustomException(ErrorCode.NOT_FOUND, "Lesson not found.");
        }
        return RestResult.success(lesson, "Lesson details retrieved successfully.");
    }

    /**
     * GET /lesson/course/{courseId}
     * 查询指定 Course 下的所有 Lesson
     */
    @GetMapping("/course/{courseId}")
    public RestResult<List<Lesson>> listLessons(@PathVariable Long courseId) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Lesson> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        List<Lesson> lessons = lessonService.list(queryWrapper);
        return RestResult.success(lessons, "Lessons retrieved successfully.");
    }
}
