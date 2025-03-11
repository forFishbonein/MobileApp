package com.tutoring.controller;

import com.tutoring.dto.LoginRequest;
import com.tutoring.dto.RegisterRequest;
import com.tutoring.dto.SendCodeRequest;
import com.tutoring.entity.User;
import com.tutoring.result.RestResult;
import com.tutoring.service.UserService;
import com.tutoring.vo.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}







