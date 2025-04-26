package com.tutoring.controller;

import com.tutoring.dto.MeetingBookingRequestDTO;
import com.tutoring.entity.MeetingBooking;
import com.tutoring.enumeration.ErrorCode;
import com.tutoring.exception.CustomException;
import com.tutoring.result.RestResult;
import com.tutoring.service.MeetingBookingService;
import com.tutoring.service.TutorAvailabilityService;
import com.tutoring.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/student/meeting")
@Slf4j
public class StudentMeetingController {

    @Autowired
    private TutorAvailabilityService availabilitySvc;
    @Autowired private MeetingBookingService bookingSvc;

    /** Student query tutor free slots */
    @GetMapping("/tutor/{tutorId}/free-slots")
    public RestResult<?> freeSlots(@PathVariable Long tutorId) {
        return RestResult.success(
                availabilitySvc.listFreeSlots(tutorId),
                "Free slots retrieved.");
    }

    /** Student create booking */
    @PostMapping("/book")
    public RestResult<?> book(@Valid @RequestBody MeetingBookingRequestDTO dto) {
        bookingSvc.createBooking(dto, SecurityUtils.getCurrentUserId());
        return RestResult.success(null, "Booking request submitted.");
    }

    /* ---------- 新增：学生查询自己的全部订单 ---------- */

    /**
     * GET /student/meeting/bookings
     * 可选参数 ?status=Pending|Confirmed|Cancelled|Expired
     */
    @GetMapping("/bookings")
    public RestResult<?> myBookings(@RequestParam(required = false) String status) {

        MeetingBooking.BookingStatus s = null;
        if (StringUtils.hasText(status)) {
            try {
                s = MeetingBooking.BookingStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                throw new CustomException(ErrorCode.BAD_REQUEST, "Invalid status value.");
            }
        }

        Long studentId = SecurityUtils.getCurrentUserId();
        if (studentId == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED, "Unauthorized or session expired.");

        return RestResult.success(
                bookingSvc.listBookings(studentId, s),
                "Bookings retrieved.");
    }
}
