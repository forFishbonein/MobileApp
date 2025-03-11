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

    /**
     * 课程的文字内容
     */
    @TableField("content")
    private String content;

    /**
     * 多张图片的链接，多个链接以逗号分隔存储
     */
    @TableField("image_urls")
    private String imageUrls;

    /**
     * 多个PDF文件的链接，多个链接以逗号分隔存储
     */
    @TableField("pdf_urls")
    private String pdfUrls;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    private boolean isCompleted;
}

