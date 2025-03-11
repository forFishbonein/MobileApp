package com.tutoring.dto;

import lombok.Data;

@Data
public class UpdateCourseRequest {
    private String name;
    private String description;
    private String subject;
}
