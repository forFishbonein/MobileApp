package com.tutoring.vo;

import com.tutoring.entity.TrainerProfile;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TrainerAllProfile {
    private TrainerProfile trainerProfile;

    private LocalDate dateOfBirth;

    private String address;
}
