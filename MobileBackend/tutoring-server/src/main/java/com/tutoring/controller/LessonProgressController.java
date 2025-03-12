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
     * POST /lessonProgress/{lessonId}/complete
     * Teacher 标记该 Lesson 下所有选课学生的进度为已完成
     */
    @PostMapping("/{lessonId}/complete")
    public RestResult<?> completeLesson(@PathVariable Long lessonId) {
        Long currentTeacherId = SecurityUtils.getCurrentUserId();
        if (currentTeacherId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "User is not authenticated.");
        }
        if (SecurityUtils.getCurrentUserRole() != User.Role.tutor) {
            throw new CustomException(ErrorCode.FORBIDDEN, "Only Teachers can mark lessons as complete.");
        }
        lessonProgressService.markLessonCompletedForAllStudents(currentTeacherId, lessonId);
        return RestResult.success(null, "Lesson marked as completed for all enrolled students.");
    }
}


