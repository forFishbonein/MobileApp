package com.tutoring.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tutoring.dto.AvailabilitySlotDTO;
import com.tutoring.entity.TutorAvailability;

import java.util.List;

public interface TutorAvailabilityService extends IService<TutorAvailability> {

    /** 老师批量设置 / 更新一周内的可用时间 */
    void updateAvailability(Long tutorId, List<AvailabilitySlotDTO> newSlots);

//    /** 查询老师从当前时刻开始的全部时段（含已预订） */
//    List<TutorAvailability> getFutureAvailability(Long tutorId);
//
//    /** 学生侧：查询指定老师、且 startTime ≥ now+1h、状态为 isBooked=false 的空闲时段 */
//    List<AvailabilitySlotDTO> listFreeSlotsForStudent(Long tutorId);

    /** Tutor 查询自己未来所有时段（含已预订） */
    List<TutorAvailability> listFutureSlots(Long tutorId);

    /** Student 查询 tutor 空闲时段（≥ now+1h，且未被预订） */
    List<AvailabilitySlotDTO> listFreeSlots(Long tutorId);
}
