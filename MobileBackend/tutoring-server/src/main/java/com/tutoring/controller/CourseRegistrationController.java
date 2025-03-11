package com.tutoring.controller;

import com.tutoring.dto.RegistrationApprovalRequest;
import com.tutoring.entity.CourseRegistration;
import com.tutoring.entity.User;
import com.tutoring.enumeration.ErrorCode;
import com.tutoring.exception.CustomException;
import com.tutoring.result.RestResult;
import com.tutoring.service.CourseRegistrationService;
import com.tutoring.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/course/registrations")
@Slf4j
@Validated
public class CourseRegistrationController {

    @Autowired
    private CourseRegistrationService courseRegistrationService;

    /**
     * GET /course/registrations
     * 导师查看待审批的注册请求
     */
    @GetMapping
    public RestResult<List<CourseRegistration>> listRegistrations() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "User is not authenticated.");
        }
        if (SecurityUtils.getCurrentUserRole() != User.Role.tutor) {
            throw new CustomException(ErrorCode.FORBIDDEN, "Only tutors can view registration requests.");
        }
        List<CourseRegistration> registrations = courseRegistrationService.findRegistrationsByTutor(currentUserId);
        return RestResult.success(registrations, "Registrations retrieved successfully.");
    }

    /**
     * PUT /course/registrations/{registrationId}
     * 导师审批注册请求
     */
    @PutMapping("/{registrationId}")
    public RestResult<?> updateRegistration(@PathVariable Long registrationId,
                                            @Valid @RequestBody RegistrationApprovalRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "User is not authenticated.");
        }
        if (SecurityUtils.getCurrentUserRole() != User.Role.tutor) {
            throw new CustomException(ErrorCode.FORBIDDEN, "Only tutors can update registration requests.");
        } 
        courseRegistrationService.updateRegistrationStatus(registrationId, request.getDecision(), currentUserId);
        return RestResult.success(null, "Registration request updated successfully.");
    }
}

