package com.tutoring.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 最终提交注册
 */
@Data
public class RegisterRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String code; // 邮箱验证码

    @NotBlank
    @Size(min = 6, message = "Password at least 6 characters")
    private String password;

    // "student" or "tutor"
    @NotBlank
    private String role;
}
