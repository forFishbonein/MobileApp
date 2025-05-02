package com.tutoring.service;

import com.tutoring.vo.TutorBasicInfoVO;

import java.util.List;

public interface StudentMeetingService {
    List<TutorBasicInfoVO> listBookableTutors(Long studentId);
}
