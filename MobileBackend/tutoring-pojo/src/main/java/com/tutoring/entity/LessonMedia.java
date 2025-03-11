package com.tutoring.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@TableName("lesson_media")
public class LessonMedia implements Serializable {

    @TableId(value = "media_id", type = IdType.AUTO)
    private Long mediaId;

    @TableField("lesson_id")
    private Long lessonId;

    @TableField("media_type")
    private MediaType mediaType;

    @TableField("media_url")
    private String mediaUrl;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 枚举对应数据库的 ENUM('PDF','AUDIO','VIDEO','IMAGE')
     */
    public enum MediaType {
        PDF, AUDIO, VIDEO, IMAGE
    }
}