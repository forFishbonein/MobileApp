package com.tutoring.controller;

import com.tutoring.dto.LoginRequest;
import com.tutoring.dto.RegisterRequest;
import com.tutoring.dto.SendCodeRequest;
import com.tutoring.dto.UpdateUserProfileRequest;
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

/**
 * 用户相关接口：注册(两步) + 登录
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 第一步：只发送验证码，不包含密码和角色
     */
    @PostMapping("/send-code")
    public RestResult<?> sendCode(@Valid @RequestBody SendCodeRequest request) {
        log.info("Send code request: {}", request);
        userService.sendEmailCode(request.getEmail());
        return RestResult.success(null,
                "Verification code has been sent to your email. Please enter it to complete registration.");
    }

    /**
     * 第二步：提交注册信息（含邮箱、验证码、密码、角色）
     * 这里注册成功后，不返回用户对象
     */
    @PostMapping("/register")
    public RestResult<?> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request: {}", request);
        userService.registerWithCode(request);
        return RestResult.success(null, "Registration successful!");
    }

    /**
     * 登录接口
     */
    @PostMapping("/login")
    public RestResult<LoginResponse> login(@Valid @RequestBody LoginRequest loginReq) {
        log.info("Login request: {}", loginReq);
        LoginResponse response = userService.login(loginReq);
        return RestResult.success(response, "Login successful.");
    }


    /**
     * 获取当前登录用户的信息
     */
    @GetMapping("/me")
    public RestResult<UserProfileResponse> getMyProfile() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "User is not authenticated or session is invalid.");
        }

        User user = userService.getUserProfile(currentUserId);

        // 转换成前端需要的 UserProfileResponse
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

    /**
     * 更新个人信息
     */
    @PutMapping("/me")
    public RestResult<?> updateMyProfile(@Valid @RequestBody UpdateUserProfileRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "User is not authenticated or session is invalid.");
        }

        userService.updateUserProfile(currentUserId, request);
        return RestResult.success(null, "Profile updated successfully.");
    }

    /**
     * 上传头像
     * 注意：这是一个 multipart/form-data 格式的请求
     */
    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RestResult<String> uploadAvatar(@RequestPart("file") MultipartFile file) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "User is not authenticated or session is invalid.");
        }

        // 调用 Service 上传并更新数据库
        String avatarUrl = userService.uploadAvatar(currentUserId, file);
        return RestResult.success(avatarUrl, "Avatar uploaded successfully.");
    }
}







