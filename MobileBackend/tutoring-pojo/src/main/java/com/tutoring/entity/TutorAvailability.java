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
@TableName("tutor_availabilities")
public class TutorAvailability implements Serializable {

    @TableId(value = "availability_id", type = IdType.AUTO)
    private Long availabilityId;

    @TableField("tutor_id")
    private Long tutorId;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("is_booked")
    private Boolean isBooked;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
