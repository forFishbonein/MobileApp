package com.tutoring.controller;

import com.tutoring.entity.User;
import com.tutoring.enumeration.ErrorCode;
import com.tutoring.exception.CustomException;
import com.tutoring.result.RestResult;
import com.tutoring.service.LessonProgressService;
import com.tutoring.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lessonProgress")
@Slf4j
public class LessonProgressController {

    @Autowired
    private LessonProgressService lessonProgressService;

    /**
     * POST /lesson/progress/{lessonId}/complete
     * 学生标记 Lesson 为已完成
     */
    @PostMapping("/{lessonId}/complete")
    public RestResult<?> completeLesson(@PathVariable Long lessonId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "User is not authenticated.");
        }
        if (SecurityUtils.getCurrentUserRole() != User.Role.student) {
            throw new CustomException(ErrorCode.FORBIDDEN, "Only students can mark lessons as complete.");
        }
        lessonProgressService.markLessonCompleted(currentUserId, lessonId);
        return RestResult.success(null, "Lesson marked as completed.");
    }
}


