package com.tutoring.controller;

import com.tutoring.entity.LessonProgress;
import com.tutoring.entity.User;
import com.tutoring.enumeration.ErrorCode;
import com.tutoring.exception.CustomException;
import com.tutoring.result.RestResult;
import com.tutoring.service.LessonProgressService;
import com.tutoring.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lessonProgress")
@Slf4j
public class LessonProgressController {

    @Autowired
    private LessonProgressService lessonProgressService;

    @PostMapping("/{lessonId}/{courseId}/completeSelf")
    public RestResult<?> completeLessonForSelf(@PathVariable Long lessonId,
                                               @PathVariable Long courseId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "User is not authenticated.");
        }
        if (SecurityUtils.getCurrentUserRole() != User.Role.student) {
            throw new CustomException(ErrorCode.FORBIDDEN, "Only students can mark their own lesson as complete.");
        }

        lessonProgressService.markLessonCompletedForStudent(currentUserId, lessonId, courseId);
        return RestResult.success(null, "Lesson progress marked as completed for the current student.");
    }


    @GetMapping("/course/{courseId}/student/{studentId}")
    public RestResult<List<LessonProgress>> getLessonProgressByCourseAndStudent(@PathVariable Long courseId,
                                                                                @PathVariable Long studentId) {

        List<LessonProgress> progressList = lessonProgressService.getLessonProgressByCourseAndStudent(courseId, studentId);
        return RestResult.success(progressList, "Query success.");
    }

}



