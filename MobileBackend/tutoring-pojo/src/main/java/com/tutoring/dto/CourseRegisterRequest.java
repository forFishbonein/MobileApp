package com.tutoring.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CourseRegisterRequest {
    @NotNull(message = "courseId cannot be null")
    private Long courseId;
    // 可选：留言等字段
    private String message;
}
