package com.tutoring.controller;

import com.tutoring.dto.*;
import com.tutoring.entity.User;
import com.tutoring.enumeration.ErrorCode;
import com.tutoring.exception.CustomException;
import com.tutoring.result.RestResult;
import com.tutoring.service.UserService;
import com.tutoring.util.SecurityUtils;
import com.tutoring.vo.LoginResponse;
import com.tutoring.vo.UserProfileResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/send-code")
    public RestResult<?> sendCode(@Valid @RequestBody SendCodeRequest request) {
        log.info("Send code request: {}", request);
        userService.sendEmailCode(request.getEmail());
        return RestResult.success(null,
                "Verification code has been sent to your email. Please enter it to complete registration.");
    }

    @PostMapping("/register")
    public RestResult<?> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request: {}", request);
        userService.registerWithCode(request);
        return RestResult.success(null, "Registration successful!");
    }

    @PostMapping("/login")
    public RestResult<LoginResponse> login(@Valid @RequestBody LoginRequest loginReq) {
        log.info("Login request: {}", loginReq);
        LoginResponse response = userService.login(loginReq);
        return RestResult.success(response, "Login successful.");
    }


    @GetMapping("/me")
    public RestResult<UserProfileResponse> getMyProfile() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "User is not authenticated or session is invalid.");
        }

        User user = userService.getUserProfile(currentUserId);

        UserProfileResponse resp = new UserProfileResponse();
        resp.setUserId(user.getUserId());
        resp.setEmail(user.getEmail());
        resp.setRole(user.getRole());
        resp.setAccountStatus(user.getAccountStatus());
        resp.setEmailVerified(user.getEmailVerified());
        resp.setNickname(user.getNickname());
        resp.setBio(user.getBio());
        resp.setAvatarUrl(user.getAvatarUrl());
        resp.setCreatedAt(user.getCreatedAt());
        resp.setUpdatedAt(user.getUpdatedAt());

        return RestResult.success(resp, "Profile fetched successfully.");
    }

    @PutMapping("/me")
    public RestResult<?> updateMyProfile(@Valid @RequestBody UpdateUserProfileRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "User is not authenticated or session is invalid.");
        }

        userService.updateUserProfile(currentUserId, request);
        return RestResult.success(null, "Profile updated successfully.");
    }

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RestResult<String> uploadAvatar(@RequestPart("file") MultipartFile file) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "User is not authenticated or session is invalid.");
        }

        String avatarUrl = userService.uploadAvatar(currentUserId, file);
        return RestResult.success(avatarUrl, "Avatar uploaded successfully.");
    }


    @PostMapping("/forgot-password")
    public RestResult<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest req) {
        userService.sendResetToken(req.getEmail());
        return RestResult.success(null, "Reset token has been sent to your email.");
    }

    @PostMapping("/reset-password")
    public RestResult<?> resetPassword(@Valid @RequestBody ResetPasswordJwtRequest req) {
        userService.resetPasswordWithToken(req);
        return RestResult.success(null, "Password has been reset successfully.");
    }
}







