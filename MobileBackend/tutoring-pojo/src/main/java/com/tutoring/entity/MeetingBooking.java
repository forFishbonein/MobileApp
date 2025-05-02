package com.tutoring.entity;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@TableName("meeting_bookings")
public class MeetingBooking implements Serializable {

    @TableId(value = "booking_id", type = IdType.AUTO)
    private Long bookingId;

    @TableField("availability_id")
    private Long availabilityId;

    @TableField("student_id")
    private Long studentId;

    @TableField("request_content")
    private String requestContent;

    @TableField("tutor_id")
    private Long tutorId;

    @TableField("status")
    private BookingStatus status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    public enum BookingStatus {
        Pending, Confirmed, Cancelled, Expired
    }
}

