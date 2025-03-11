package com.tutoring.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseDetailResponse {
    private Long courseId;
    private Long tutorId;
    private String courseName;
    private String description;
    private String subject;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
