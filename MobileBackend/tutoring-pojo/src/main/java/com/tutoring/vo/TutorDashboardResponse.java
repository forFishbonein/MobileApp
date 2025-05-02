package com.tutoring.vo;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TutorDashboardResponse {
    private Long   courseId;
    private String courseName;
    private Integer studentCount;
    private List<StudentProgressVO> students;
}