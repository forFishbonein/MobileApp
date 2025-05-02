package com.tutoring.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tutoring.dto.AvailabilitySlotDTO;
import com.tutoring.entity.TutorAvailability;

import java.util.List;

public interface TutorAvailabilityService extends IService<TutorAvailability> {

    void updateAvailability(Long tutorId, List<AvailabilitySlotDTO> newSlots);

    List<TutorAvailability> listFutureSlots(Long tutorId);

    List<AvailabilitySlotDTO> listFreeSlots(Long tutorId);
}
