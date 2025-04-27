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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tutor/dashboard")
@Slf4j
public class TutorDashboardController {

    @Autowired private TutorDashboardService dashboardSvc;

    /**
     * GET /tutor/dashboard
     * GET /tutor/dashboard?courseId=123
     */
    @GetMapping
    public RestResult<?> dashboard(@RequestParam(required = false) Long courseId) {
        Long tutorId = SecurityUtils.getCurrentUserId();
        if (tutorId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED,
                    "User is not authenticated or session is invalid.");
        }

        if (courseId != null) {
            // 单课程
            TutorDashboardResponse vo =
                    dashboardSvc.getDashboardData(tutorId, courseId);
            return RestResult.success(vo, "Dashboard data retrieved.");
        } else {
            // 全部课程
            List<TutorDashboardResponse> all =
                    dashboardSvc.getAllDashboardData(tutorId);
            return RestResult.success(all, "Dashboard data for all courses retrieved.");
        }
    }
}
