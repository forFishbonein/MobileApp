package com.tutoring.service;

import com.tutoring.vo.TutorDashboardResponse;

public interface TutorDashboardService {
    /**
     * 获取当前导师仪表盘数据，显示他们所教授的每门课程的注册学生人数
     * 以及各学生在各课程单元（Lesson）中的学习进度。
     *
     * @param tutorId 当前导师的ID
     * @return 仪表盘数据 DTO
     */
    TutorDashboardResponse getDashboardData(Long tutorId);
}
