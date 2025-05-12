package com.tutoring.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutoring.dao.TutorAvailabilityDao;
import com.tutoring.dto.AvailabilitySlotDTO;
import com.tutoring.entity.TutorAvailability;
import com.tutoring.service.TutorAvailabilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;


@Slf4j
@Service
public class TutorAvailabilityServiceImpl extends ServiceImpl<TutorAvailabilityDao, TutorAvailability>
        implements TutorAvailabilityService {
    private static final int DAYS_AHEAD = 30;

    @Override
    @Transactional
    public void addAvailability(Long tutorId, List<AvailabilitySlotDTO> slots) {

        if (CollectionUtils.isEmpty(slots)) {
            log.warn("Tutor[{}] addAvailability called with empty list.", tutorId);
            return;
        }

        LocalDateTime now   = LocalDateTime.now();
        LocalDateTime limit = now.plusDays(DAYS_AHEAD);

        Map<String, AvailabilitySlotDTO> uniq = slots.stream()
                .filter(s -> s.getStartTime() != null && s.getEndTime() != null)
                .filter(s -> !s.getStartTime().isBefore(now))
                .filter(s ->  s.getEndTime().isAfter(s.getStartTime()))
                .filter(s -> !s.getEndTime().isAfter(limit))
                .collect(Collectors.toMap(
                        s -> keyOf(s),
                        Function.identity(),
                        (oldVal, newVal) -> oldVal,
                        LinkedHashMap::new));

        if (uniq.isEmpty()) {
            log.info("Tutor[{}] addAvailability – all slots invalid / >30d.", tutorId);
            return;
        }

        Set<String> dbKeys = lambdaQuery()
                .eq(TutorAvailability::getTutorId, tutorId)
                .ge(TutorAvailability::getStartTime, now)
                .le(TutorAvailability::getEndTime,   limit)
                .list()
                .stream()
                .map(v -> keyOf(v.getStartTime(), v.getEndTime()))
                .collect(Collectors.toSet());

        List<TutorAvailability> toInsert = uniq.values().stream()
                .filter(s -> !dbKeys.contains(keyOf(s)))
                .map(s -> TutorAvailability.builder()
                        .tutorId(tutorId)
                        .startTime(s.getStartTime())
                        .endTime(s.getEndTime())
                        .isBooked(false)
                        .build())
                .collect(Collectors.toList());

        if (toInsert.isEmpty()) {
            log.info("Tutor[{}] addAvailability – nothing new (all duplicates).", tutorId);
            return;
        }

        saveBatch(toInsert);
        log.info("Tutor[{}] added {} new availability slots.", tutorId, toInsert.size());
    }

    private static String keyOf(AvailabilitySlotDTO dto){
        return keyOf(dto.getStartTime(), dto.getEndTime());
    }
    private static String keyOf(LocalDateTime start, LocalDateTime end){
        return start + "_" + end;
    }


    @Override
    public List<TutorAvailability> listFutureSlots(Long tutorId) {
        return lambdaQuery()
                .eq(TutorAvailability::getTutorId, tutorId)
                .ge(TutorAvailability::getStartTime, LocalDateTime.now())
                .orderByAsc(TutorAvailability::getStartTime)
                .list();
    }

    @Override
    public List<AvailabilitySlotDTO> listFreeSlots(Long tutorId) {
        LocalDateTime cutoff = LocalDateTime.now().plusHours(1);
        return lambdaQuery()
                .eq(TutorAvailability::getTutorId, tutorId)
                .eq(TutorAvailability::getIsBooked, false)
                .ge(TutorAvailability::getStartTime, cutoff)
                .orderByAsc(TutorAvailability::getStartTime)
                .list()
                .stream()
                .map(v -> AvailabilitySlotDTO.builder()
                        .availabilityId(v.getAvailabilityId())
                        .startTime(v.getStartTime())
                        .endTime(v.getEndTime())
                        .build())
                .collect(Collectors.toList());
    }
}
