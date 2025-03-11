package com.tutoring.vo;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TutorCourseResponse {
    private Long courseId;
    private String courseName;
    private String description;
    private String subject;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
