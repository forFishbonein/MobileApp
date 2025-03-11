package com.tutoring.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateLessonRequest {

    private Long courseId;  // 关联的课程ID

    private String title;

    private String content; // 课程文字内容

    /**
     * 多张图片的链接，用逗号分隔（可选）
     */
    private String imageUrls;

    /**
     * 多个PDF链接，用逗号分隔（可选）
     */
    private String pdfUrls;

    /**
     * 末尾标记，表示该 Lesson 是否已完成
     */
    private Boolean isCompleted;
}
