package com.tutoring.vo;

import com.tutoring.entity.MeetingBooking;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingBookingVO {

    private Long bookingId;

    private Long tutorId;
    private String tutorNickname;

    private String content;                               // 学生填写的说明
    private MeetingBooking.BookingStatus status;

    private LocalDateTime startTime;                      // 对应 TutorAvailability
    private LocalDateTime endTime;

    private LocalDateTime createdAt;
}
