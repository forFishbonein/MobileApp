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

    /**
     * 学生端接口：学生为自己标记某个 lesson 已完成
     * 若原先无记录，则创建一条进度记录并设为 completed；若已存在，则直接更新为 completed。
     * 前端会同时传 lessonId 和 courseId。
     */
    @PostMapping("/{lessonId}/{courseId}/completeSelf")
    public RestResult<?> completeLessonForSelf(@PathVariable Long lessonId,
                                               @PathVariable Long courseId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "User is not authenticated.");
        }
        // 判断用户角色是否为学生（若项目中学生角色为 'student'，可自行调整）
        if (SecurityUtils.getCurrentUserRole() != User.Role.student) {
            throw new CustomException(ErrorCode.FORBIDDEN, "Only students can mark their own lesson as complete.");
        }

        // 调用 service 更新或插入
        lessonProgressService.markLessonCompletedForStudent(currentUserId, lessonId, courseId);
        return RestResult.success(null, "Lesson progress marked as completed for the current student.");
    }


    /**
     * 2. 查询接口：根据 courseId + studentId 查出该学生在此课程下所有 lesson 的进度
     */
    @GetMapping("/course/{courseId}/student/{studentId}")
    public RestResult<List<LessonProgress>> getLessonProgressByCourseAndStudent(@PathVariable Long courseId,
                                                                                @PathVariable Long studentId) {
        // 如果需要做权限判断（例如，只能查自己的进度，或教师可查等），可在这里加相应逻辑
        //
        // if (!hasPermission(currentUserId, studentId)) {
        //     throw new CustomException(ErrorCode.FORBIDDEN, "No permission.");
        // }

        List<LessonProgress> progressList = lessonProgressService.getLessonProgressByCourseAndStudent(courseId, studentId);
        return RestResult.success(progressList, "Query success.");
    }

}



