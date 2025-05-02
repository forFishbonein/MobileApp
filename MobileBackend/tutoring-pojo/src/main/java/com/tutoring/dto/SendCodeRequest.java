package com.tutoring.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class SendCodeRequest {

    @Email
    @NotBlank
    private String email;
}
