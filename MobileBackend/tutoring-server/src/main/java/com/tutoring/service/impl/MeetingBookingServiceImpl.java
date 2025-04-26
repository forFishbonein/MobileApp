package com.tutoring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutoring.dao.MeetingBookingDao;
import com.tutoring.dto.MeetingBookingRequestDTO;
import com.tutoring.entity.MeetingBooking;
import com.tutoring.entity.TutorAvailability;
import com.tutoring.entity.User;
import com.tutoring.enumeration.ErrorCode;
import com.tutoring.exception.CustomException;
import com.tutoring.service.MeetingBookingService;
import com.tutoring.service.TutorAvailabilityService;
import com.tutoring.service.UserService;
import com.tutoring.vo.MeetingBookingVO;
import com.tutoring.vo.MeetingRequestVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MeetingBookingServiceImpl extends ServiceImpl<MeetingBookingDao, MeetingBooking>
        implements MeetingBookingService {

    @Autowired
    private TutorAvailabilityService availabilitySvc;
    @Autowired private UserService userSvc;

    @Override
    public void createBooking(MeetingBookingRequestDTO dto, Long studentId) {

        TutorAvailability slot = availabilitySvc.getById(dto.getAvailabilityId());
        if (slot == null)
            throw new CustomException(ErrorCode.NOT_FOUND, "Selected slot not found.");
        if (slot.getIsBooked())
            throw new CustomException(ErrorCode.BAD_REQUEST, "Slot already booked.");
        if (slot.getStartTime().isBefore(LocalDateTime.now().plusHours(1)))
            throw new CustomException(ErrorCode.BAD_REQUEST, "Slot is too close to current time.");

        // 防重复
        boolean dup = lambdaQuery()
                .eq(MeetingBooking::getStudentId, studentId)
                .eq(MeetingBooking::getAvailabilityId, slot.getAvailabilityId())
                .in(MeetingBooking::getStatus,
                        MeetingBooking.BookingStatus.Pending,
                        MeetingBooking.BookingStatus.Confirmed)
                .count() > 0;
        if (dup)
            throw new CustomException(ErrorCode.BAD_REQUEST, "Duplicate booking request.");

        MeetingBooking booking = MeetingBooking.builder()
                .availabilityId(slot.getAvailabilityId())
                .studentId(studentId)
                .tutorId(slot.getTutorId())
                .requestContent(dto.getContent())
                .status(MeetingBooking.BookingStatus.Pending)
                .build();
        save(booking);

        // 锁定时段
        slot.setIsBooked(true);
        availabilitySvc.updateById(slot);
    }

    @Override
    public void approve(Long id, Long tutorId, String comment) {
        MeetingBooking b = getById(id);
        if (b == null || !b.getTutorId().equals(tutorId))
            throw new CustomException(ErrorCode.NOT_FOUND, "Booking not found.");

        if (b.getStatus() != MeetingBooking.BookingStatus.Pending)
            throw new CustomException(ErrorCode.BAD_REQUEST, "Booking already processed.");

        b.setStatus(MeetingBooking.BookingStatus.Confirmed);
        updateById(b);
        // TODO: send notification (comment)
    }

    @Override
    public void reject(Long id, Long tutorId, String comment) {
        MeetingBooking b = getById(id);
        if (b == null || !b.getTutorId().equals(tutorId))
            throw new CustomException(ErrorCode.NOT_FOUND, "Booking not found.");

        if (b.getStatus() != MeetingBooking.BookingStatus.Pending)
            throw new CustomException(ErrorCode.BAD_REQUEST, "Booking already processed.");

        b.setStatus(MeetingBooking.BookingStatus.Cancelled);
        updateById(b);

        // 释放时段
        TutorAvailability slot = availabilitySvc.getById(b.getAvailabilityId());
        if (slot != null) {
            slot.setIsBooked(false);
            availabilitySvc.updateById(slot);
        }
        // TODO: send notification (comment)
    }

    @Override
    public List<MeetingRequestVO> listPending(Long tutorId) {

        return lambdaQuery()
                .eq(MeetingBooking::getTutorId, tutorId)
//                .eq(MeetingBooking::getStatus, MeetingBooking.BookingStatus.Pending)
                .orderByAsc(MeetingBooking::getCreatedAt)
                .list()
                .stream()
                .map(b -> {
                    User stu = userSvc.getById(b.getStudentId());
                    TutorAvailability slot = availabilitySvc.getById(b.getAvailabilityId());
                    return MeetingRequestVO.builder()
                            .bookingId(b.getBookingId())
                            .studentId(b.getStudentId())
                            .studentNickname(stu == null ? "Unknown" : stu.getNickname())
                            .content(b.getRequestContent())
                            .startTime(slot.getStartTime())
                            .endTime(slot.getEndTime())
                            .status(b.getStatus())
                            .createdAt(b.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<MeetingBookingVO> listBookings(Long studentId,
                                               MeetingBooking.BookingStatus status) {

        LambdaQueryWrapper<MeetingBooking> qw = new LambdaQueryWrapper<>();
        qw.eq(MeetingBooking::getStudentId, studentId)
                .orderByDesc(MeetingBooking::getCreatedAt);
        if (status != null) {
            qw.eq(MeetingBooking::getStatus, status);
        }
        List<MeetingBooking> list = list(qw);
        if (list.isEmpty()) return Collections.emptyList();

        // 批量取 Tutor 和 Availability，避免 N+1
        Set<Long> tutorIds = list.stream()
                .map(MeetingBooking::getTutorId)
                .collect(Collectors.toSet());
        Map<Long,String> tutorMap = userSvc.listByIds(tutorIds).stream()
                .collect(Collectors.toMap(User::getUserId, User::getNickname));

        Set<Long> availIds = list.stream()
                .map(MeetingBooking::getAvailabilityId)
                .collect(Collectors.toSet());
        Map<Long,TutorAvailability> slotMap =
                availabilitySvc.listByIds(availIds).stream()
                        .collect(Collectors.toMap(
                                TutorAvailability::getAvailabilityId,
                                v -> v));

        return list.stream().map(b -> {
            TutorAvailability slot = slotMap.get(b.getAvailabilityId());
            return MeetingBookingVO.builder()
                    .bookingId(b.getBookingId())
                    .tutorId(b.getTutorId())
                    .tutorNickname(tutorMap.getOrDefault(b.getTutorId(), "Unknown"))
                    .content(b.getRequestContent())
                    .status(b.getStatus())
                    .startTime(slot == null ? null : slot.getStartTime())
                    .endTime(slot == null ? null : slot.getEndTime())
                    .createdAt(b.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());
    }
}
