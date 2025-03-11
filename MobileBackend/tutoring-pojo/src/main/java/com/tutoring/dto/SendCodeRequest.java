package com.tutoring.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 用于请求发送验证码的请求体
 */
@Data
public class SendCodeRequest {

    @Email
    @NotBlank
    private String email;
}
