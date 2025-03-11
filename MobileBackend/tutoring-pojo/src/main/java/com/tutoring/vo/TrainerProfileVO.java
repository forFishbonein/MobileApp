package com.tutoring.vo;

import com.tutoring.entity.TrainerConnectRequest;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TrainerProfileVO {
    // 这里可能要带上trainer的userid
    private Long userId;

    private String name;

    private String certifications;

    private String specializations;

    private Integer yearsOfExperience;

    private String biography;

    private String workplace;

    private TrainerConnectRequest.RequestStatus connectStatus;

}
