package com.tutoring.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import com.tutoring.entity.MeetingBooking;

/**
 * 登录成功后返回
 */
@Data
@Builder
public class MeetingRequestVO {
    private Long bookingId;
    private Long studentId;
    private String studentNickname;
    private String content;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private MeetingBooking.BookingStatus status;
    private LocalDateTime createdAt;
}
