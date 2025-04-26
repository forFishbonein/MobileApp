package com.tutoring.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ResetPasswordJwtRequest {
    private String token;         // 用户从邮件里复制的 JWT
    @NotBlank
    private String newPassword;
}
