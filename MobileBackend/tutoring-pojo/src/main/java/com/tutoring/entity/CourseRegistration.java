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
@TableName("course_registrations")
public class CourseRegistration implements Serializable {

    @TableId(value = "registration_id", type = IdType.AUTO)
    private Long registrationId;

    @TableField("course_id")
    private Long courseId;

    @TableField("student_id")
    private Long studentId;

    @TableField("status")
    private RegistrationStatus status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 枚举对应数据库的 ENUM('pending','approved','rejected')
     */
    public enum RegistrationStatus {
        pending, approved, rejected
    }
}

