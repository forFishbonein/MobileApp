package com.tutoring.controller;

import com.tutoring.dto.MeetingBookingRequestDTO;
import com.tutoring.result.RestResult;
import com.tutoring.service.MeetingBookingService;
import com.tutoring.service.TutorAvailabilityService;
import com.tutoring.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
}
