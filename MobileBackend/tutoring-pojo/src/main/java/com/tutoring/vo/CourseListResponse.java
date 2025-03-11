package com.tutoring.vo;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CourseListResponse {
    private Long courseId;
    private String courseName;
    private String description;
    private String subject;
    private String teacherName;  // 教师昵称
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
