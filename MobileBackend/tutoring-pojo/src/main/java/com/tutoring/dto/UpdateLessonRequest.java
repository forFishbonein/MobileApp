package com.tutoring.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateLessonRequest {

    @NotBlank(message = "Lesson title cannot be blank")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    private String content;

    private String imageUrls;

    private String pdfUrls;

    private Boolean isCompleted;
}
