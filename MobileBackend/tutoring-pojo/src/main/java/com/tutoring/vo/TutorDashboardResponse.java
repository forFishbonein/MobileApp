package com.tutoring.vo;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TutorDashboardResponse {
    private Long   courseId;
    private String courseName;
    /** 选课学生数（approved） */
    private Integer studentCount;
    /** 每个学生的学习进度 */
    private List<StudentProgressVO> students;
}