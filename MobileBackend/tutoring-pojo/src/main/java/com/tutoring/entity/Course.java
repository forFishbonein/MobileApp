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
@TableName("courses")
public class Course implements Serializable {

    @TableId(value = "course_id", type = IdType.AUTO)
    private Long courseId;

    @TableField("tutor_id")
    private Long tutorId;

    @TableField("course_name")
    private String courseName;

    @TableField("description")
    private String description;

    @TableField("subject")
    private String subject;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
