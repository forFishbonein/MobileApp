package com.tutoring.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tutoring.dto.MeetingBookingRequestDTO;
import com.tutoring.entity.MeetingBooking;
import com.tutoring.vo.MeetingBookingVO;
import com.tutoring.vo.MeetingRequestVO;

import java.util.List;

public interface MeetingBookingService extends IService<MeetingBooking> {
    void createBooking(MeetingBookingRequestDTO dto, Long studentId);

    void approve(Long bookingId, Long tutorId, String comment);

    void reject (Long bookingId, Long tutorId, String comment);

    List<MeetingRequestVO> listPending(Long tutorId);

    List<MeetingBookingVO> listBookings(Long studentId, MeetingBooking.BookingStatus status);
}
