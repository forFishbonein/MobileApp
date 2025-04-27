package com.tutoring.service;

import com.tutoring.vo.TutorBasicInfoVO;

import java.util.List;

public interface StudentMeetingService {
    /**
     * 列出当前学生可预约的所有老师：
     * 即该学生所有已通过的课程注册所对应的 tutor。
     */
    List<TutorBasicInfoVO> listBookableTutors(Long studentId);
}
