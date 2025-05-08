package com.tutoring.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tutoring.dto.AvailabilitySlotDTO;
import com.tutoring.entity.TutorAvailability;

import java.util.List;

public interface TutorAvailabilityService extends IService<TutorAvailability> {

    // void updateAvailability(Long tutorId, List<AvailabilitySlotDTO> newSlots);

    /** 只做插入，无更新 / 删除逻辑 */
    void addAvailability(Long tutorId, List<AvailabilitySlotDTO> slots);

    List<TutorAvailability> listFutureSlots(Long tutorId);

    List<AvailabilitySlotDTO> listFreeSlots(Long tutorId);
}
