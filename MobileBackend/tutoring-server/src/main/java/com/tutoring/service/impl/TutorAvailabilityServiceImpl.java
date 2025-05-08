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
import java.util.stream.Collectors;


@Slf4j
@Service
public class TutorAvailabilityServiceImpl extends ServiceImpl<TutorAvailabilityDao, TutorAvailability>
        implements TutorAvailabilityService {

    private static final int RANGE_DAYS = 7;

//    @Override
//    @Transactional
//    public void updateAvailability(Long tutorId, List<AvailabilitySlotDTO> newSlots) {
//
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime limit = now.plusDays(RANGE_DAYS);
//
//        List<AvailabilitySlotDTO> valid = newSlots.stream()
//                .filter(s -> !s.getStartTime().isBefore(now) &&
//                        !s.getEndTime().isAfter(limit) &&
//                        s.getEndTime().isAfter(s.getStartTime()))
//                .collect(Collectors.toList());
//
//        List<TutorAvailability> existing = lambdaQuery()
//                .eq(TutorAvailability::getTutorId, tutorId)
//                .ge(TutorAvailability::getStartTime, now)
//                .le(TutorAvailability::getEndTime, limit)
//                .list();
//
//        Map<String, TutorAvailability> existMap = existing.stream()
//                .collect(Collectors.toMap(
//                        v -> v.getStartTime() + "_" + v.getEndTime(),
//                        v -> v));
//
//        Set<String> newKeys = valid.stream()
//                .map(v -> v.getStartTime() + "_" + v.getEndTime())
//                .collect(Collectors.toSet());
//
//        List<TutorAvailability> toInsert = valid.stream()
//                .filter(v -> !existMap.containsKey(v.getStartTime() + "_" + v.getEndTime()))
//                .map(v -> TutorAvailability.builder()
//                        .tutorId(tutorId)
//                        .startTime(v.getStartTime())
//                        .endTime(v.getEndTime())
//                        .isBooked(false)
//                        .build())
//                .collect(Collectors.toList());
//        if (!toInsert.isEmpty()) this.saveBatch(toInsert);
//
//        List<Long> toDelete = existing.stream()
//                .filter(v -> !newKeys.contains(v.getStartTime() + "_" + v.getEndTime())
//                        && !Boolean.TRUE.equals(v.getIsBooked()))
//                .map(TutorAvailability::getAvailabilityId)
//                .collect(Collectors.toList());
//        if (!toDelete.isEmpty()) this.removeByIds(toDelete);
//
//    }
    /** 允许前端一次性添加未来 30 天内的时间段 */
    private static final int DAYS_AHEAD = 30;

    /**
     * 只插入新时间段：
     *  - 校验时间合法性
     *  - 超过 30 天的忽略
     *  - start 需 < end
     */
    @Override
    @Transactional
    public void addAvailability(Long tutorId, List<AvailabilitySlotDTO> slots) {

        if (CollectionUtils.isEmpty(slots)) {
            log.warn("Tutor[{}] addAvailability called with empty list.", tutorId);
            return;
        }

        LocalDateTime now   = LocalDateTime.now();
        LocalDateTime limit = now.plusDays(DAYS_AHEAD);

        List<TutorAvailability> toInsert = slots.stream()
                .filter(s -> s.getStartTime() != null && s.getEndTime() != null)
                .filter(s -> !s.getStartTime().isBefore(now))      // start ≥ now
                .filter(s ->  s.getEndTime().isAfter(s.getStartTime()))
                .filter(s -> !s.getEndTime().isAfter(limit))       // end ≤ now+30d
                .map(s -> TutorAvailability.builder()
                        .tutorId(tutorId)
                        .startTime(s.getStartTime())
                        .endTime(s.getEndTime())
                        .isBooked(false)
                        .build())
                .collect(Collectors.toList());

        /* 如果全部都被排除，只记录日志即可 */
        if (toInsert.isEmpty()) {
            log.info("Tutor[{}] tried to add slots – all invalid or >30 days.", tutorId);
            return;
        }

        saveBatch(toInsert);
        log.info("Tutor[{}] added {} availability slots.", tutorId, toInsert.size());
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
