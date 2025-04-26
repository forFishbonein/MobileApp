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

    /** Tutor 查看待审批 */
    List<MeetingRequestVO> listPending(Long tutorId);

    /** 学生查询自己的全部订单（可按状态筛选，null=全部） */
    List<MeetingBookingVO> listBookings(Long studentId, MeetingBooking.BookingStatus status);
}
