package com.tutoring.service;

import com.tutoring.vo.TutorDashboardResponse;

import java.util.List;

public interface TutorDashboardService {
    TutorDashboardResponse getDashboardData(Long tutorId, Long courseId);

    List<TutorDashboardResponse> getAllDashboardData(Long tutorId);
}
