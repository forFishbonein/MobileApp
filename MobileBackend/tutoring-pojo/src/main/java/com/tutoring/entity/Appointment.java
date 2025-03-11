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
@TableName("appointments")
public class Appointment implements Serializable {

    @TableId(value = "appointment_id", type = IdType.AUTO)
    private Long appointmentId;

    @TableField("availability_id")
    private Long availabilityId;

    @TableField("student_id")
    private Long studentId;

    @TableField("status")
    private AppointmentStatus status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 枚举对应数据库的 ENUM('scheduled','completed','cancelled')
     */
    public enum AppointmentStatus {
        scheduled, completed, cancelled
    }
}
