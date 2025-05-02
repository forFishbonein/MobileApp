package com.tutoring.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CourseRegisterRequest {
    @NotNull(message = "courseId cannot be null")
    private Long courseId;
    private String message;
}
