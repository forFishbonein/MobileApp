package com.tutoring.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ResetPasswordJwtRequest {
    private String token;
    @NotBlank
    private String newPassword;
}
