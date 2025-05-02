package com.tutoring.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateLessonRequest {

    private Long courseId;

    private String title;

    private String content;

    private String imageUrls;

    private String pdfUrls;

    private Boolean isCompleted;
}
