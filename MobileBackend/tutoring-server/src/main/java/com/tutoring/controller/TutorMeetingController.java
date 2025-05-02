package com.tutoring.controller;

import com.tutoring.dto.TutorAvailabilityDTO;
import com.tutoring.entity.TutorAvailability;
import com.tutoring.enumeration.ErrorCode;
import com.tutoring.exception.CustomException;
import com.tutoring.result.RestResult;
import com.tutoring.service.MeetingBookingService;
import com.tutoring.service.TutorAvailabilityService;
import com.tutoring.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tutor/meeting")
@Slf4j
public class TutorMeetingController {

    @Autowired private TutorAvailabilityService availabilitySvc;
    @Autowired private MeetingBookingService bookingSvc;

    @GetMapping("/availability")
    public RestResult<?> mySlots() {
        Long tutorId = SecurityUtils.getCurrentUserId();
        if (tutorId == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED, "Unauthorized or session expired.");
        return RestResult.success(
                availabilitySvc.listFutureSlots(tutorId),
                "Slots retrieved.");
    }

    @PostMapping("/availability")
    public RestResult<?> saveSlots(@Valid @RequestBody TutorAvailabilityDTO dto) {
        availabilitySvc.updateAvailability(
                SecurityUtils.getCurrentUserId(), dto.getAvailabilitySlots());
        return RestResult.success(null, "Slots updated.");
    }

    @GetMapping("/requests/pending")
    public RestResult<?> pending() {
        return RestResult.success(
                bookingSvc.listPending(SecurityUtils.getCurrentUserId()),
                "Pending bookings retrieved.");
    }

    @PutMapping("/requests/{id}/approve")
    public RestResult<?> approve(@PathVariable Long id,
                                 @RequestBody(required=false) String comment) {
        bookingSvc.approve(id, SecurityUtils.getCurrentUserId(), comment);
        return RestResult.success(null, "Booking approved.");
    }

    @PutMapping("/requests/{id}/reject")
    public RestResult<?> reject(@PathVariable Long id,
                                @RequestBody(required=false) String comment) {
        bookingSvc.reject(id, SecurityUtils.getCurrentUserId(), comment);
        return RestResult.success(null, "Booking rejected.");
    }
}

