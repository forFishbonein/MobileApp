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

    private String content;
    private MeetingBooking.BookingStatus status;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private LocalDateTime createdAt;
}
