package com.tutoring.service;

import com.tutoring.vo.TutorDashboardResponse;

import java.util.List;

public interface TutorDashboardService {
    /**
     * 单课程 Dashboard
     */
    TutorDashboardResponse getDashboardData(Long tutorId, Long courseId);

    /**
     * 所有课程 Dashboard
     */
    List<TutorDashboardResponse> getAllDashboardData(Long tutorId);
}
