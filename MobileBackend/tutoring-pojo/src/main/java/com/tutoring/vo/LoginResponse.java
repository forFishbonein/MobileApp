package com.tutoring.vo;

import com.tutoring.entity.User;
import lombok.Data;

/**
 * 登录成功后返回
 */
@Data
public class LoginResponse {
    private String token;
    private Long userId;
    private User.Role role;
}
