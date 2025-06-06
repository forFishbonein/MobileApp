package com.tutoring.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@TableName("lessons")
public class Lesson implements Serializable {

    @TableId(value = "lesson_id", type = IdType.AUTO)
    private Long lessonId;

    @TableField("course_id")
    private Long courseId;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("image_urls")
    private String imageUrls;

    @TableField("pdf_urls")
    private String pdfUrls;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(value = "is_completed", fill = FieldFill.INSERT_UPDATE)
    private boolean isCompleted;
}

