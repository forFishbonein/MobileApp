package com.tutoring.dto;

import lombok.Data;

@Data
public class CreateCourseRequest {

    private String name;

    private String description;

    private String subject;
}
