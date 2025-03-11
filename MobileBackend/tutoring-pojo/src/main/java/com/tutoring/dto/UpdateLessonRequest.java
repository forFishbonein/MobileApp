package com.tutoring.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateLessonRequest {

    @NotBlank(message = "Lesson title cannot be blank")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    private String content;

    /**
     * 多张图片链接，多个链接用逗号分隔（可选）
     */
    private String imageUrls;

    /**
     * 多个PDF链接，多个链接用逗号分隔（可选）
     */
    private String pdfUrls;

    private Boolean isCompleted;
}
