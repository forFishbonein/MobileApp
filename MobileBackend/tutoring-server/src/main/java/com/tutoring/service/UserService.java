package com.tutoring.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tutoring.dto.LoginRequest;
import com.tutoring.dto.RegisterRequest;
import com.tutoring.entity.User;
import com.tutoring.vo.LoginResponse;

public interface UserService extends IService<User> {

    /**
     * 发送邮箱验证码（只传 email）
     */
    void sendEmailCode(String email);

    /**
     * 第二步：校验验证码 + 注册用户
     */
    User registerWithCode(RegisterRequest request);

    /**
     * 登录
     */
    LoginResponse login(LoginRequest loginReq);
}
