package com.tutoring.controller;

import com.tutoring.entity.User;
import com.tutoring.enumeration.ErrorCode;
import com.tutoring.exception.CustomException;
import com.tutoring.result.RestResult;
import com.tutoring.service.TutorDashboardService;
import com.tutoring.util.SecurityUtils;
import com.tutoring.vo.TutorDashboardResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tutor/dashboard")
@Slf4j
public class TutorDashboardController {

    @Autowired
    private TutorDashboardService tutorDashboardService;

    /**
     * GET /tutor/dashboard
     * 获取当前导师仪表盘数据，显示各课程注册学生数以及各学生各Lesson的学习进度
     */
    @GetMapping
    public RestResult<TutorDashboardResponse> getDashboard() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "User is not authenticated or session is invalid.");
        }
        if (SecurityUtils.getCurrentUserRole() != User.Role.tutor) {
            throw new CustomException(ErrorCode.FORBIDDEN, "Only tutors can view dashboard.");
        }
        TutorDashboardResponse dashboardData = tutorDashboardService.getDashboardData(currentUserId);
        return RestResult.success(dashboardData, "Dashboard data retrieved successfully.");
    }
}
