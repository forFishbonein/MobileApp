package com.tutoring.vo;

import com.tutoring.entity.User;
import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private Long userId;
    private User.Role role;
}
