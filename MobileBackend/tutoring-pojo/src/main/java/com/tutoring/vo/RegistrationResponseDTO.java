package com.tutoring.vo;

import com.tutoring.entity.CourseRegistration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationResponseDTO {
    private Long registrationId;
    private Long courseId;
    private Long studentId;
    private String studentNickname;
    private CourseRegistration.RegistrationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

