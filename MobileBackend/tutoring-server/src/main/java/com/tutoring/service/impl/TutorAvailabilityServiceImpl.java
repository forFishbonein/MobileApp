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
    /** 允许前端一次性添加未来 30 天内的时间段 */
    private static final int DAYS_AHEAD = 30;

    /**
     * 仅执行“新增”：
     * 1. 校验时间合法 & 去重
     * 2. 过滤掉数据库已有的同段
     * 3. 批量插入真正新增的记录
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

        /* ---------- 1. 基本校验 + List 内部去重 ---------- */
        Map<String, AvailabilitySlotDTO> uniq = slots.stream()
                .filter(s -> s.getStartTime() != null && s.getEndTime() != null)
                .filter(s -> !s.getStartTime().isBefore(now))             // start ≥ now
                .filter(s ->  s.getEndTime().isAfter(s.getStartTime()))   // end   > start
                .filter(s -> !s.getEndTime().isAfter(limit))              // end ≤ now+30d
                .collect(Collectors.toMap(
                        s -> keyOf(s),            // key = "start_end"
                        Function.identity(),
                        (oldVal, newVal) -> oldVal,          // 去重：保留第一次出现
                        LinkedHashMap::new));                // 保持顺序──可选

        if (uniq.isEmpty()) {
            log.info("Tutor[{}] addAvailability – all slots invalid / >30d.", tutorId);
            return;
        }

        /* ---------- 2. 拉出 tutor 现有空闲段构建 key-set ---------- */
        Set<String> dbKeys = lambdaQuery()
                .eq(TutorAvailability::getTutorId, tutorId)
                .ge(TutorAvailability::getStartTime, now)
                .le(TutorAvailability::getEndTime,   limit)
                .list()
                .stream()
                .map(v -> keyOf(v.getStartTime(), v.getEndTime()))
                .collect(Collectors.toSet());

        /* ---------- 3. 只留下真正“新增”的 ---------- */
        List<TutorAvailability> toInsert = uniq.values().stream()
                .filter(s -> !dbKeys.contains(keyOf(s)))          // 表中不存在
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

        /* ---------- 4. 批量落库 ---------- */
        saveBatch(toInsert);
        log.info("Tutor[{}] added {} new availability slots.", tutorId, toInsert.size());
    }

    /* key 生成工具（保持统一） */
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
