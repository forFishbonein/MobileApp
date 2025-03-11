package com.tutoring.controller;

import com.tutoring.dto.LoginRequest;
import com.tutoring.dto.RegisterRequest;
import com.tutoring.dto.SendCodeRequest;
import com.tutoring.entity.User;
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
    public String sendCode(@Valid @RequestBody SendCodeRequest request) {
        log.info("Send code request: {}", request);
        userService.sendEmailCode(request.getEmail());
        return "Verification code has been sent to your email. Please enter it to complete registration.";
    }

    /**
     * 第二步：提交注册信息（含邮箱、验证码、密码、角色）
     */
    @PostMapping("/register")
    public User register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request: {}", request);
        User newUser = userService.registerWithCode(request);

        // 不返回密码给前端
        newUser.setPasswordHash(null);
        return newUser;
    }

    /**
     * 登录接口
     */
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginReq) {
        log.info("Login request: {}", loginReq);
        return userService.login(loginReq);
    }
}
