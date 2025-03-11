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
@TableName("lesson_progress")
public class LessonProgress implements Serializable {

    @TableId(value = "progress_id", type = IdType.AUTO)
    private Long progressId;

    @TableField("student_id")
    private Long studentId;

    @TableField("lesson_id")
    private Long lessonId;

    @TableField("status")
    private ProgressStatus status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 枚举对应数据库的 ENUM('not_started','in_progress','completed')
     */
    public enum ProgressStatus {
        not_started, in_progress, completed
    }
}
