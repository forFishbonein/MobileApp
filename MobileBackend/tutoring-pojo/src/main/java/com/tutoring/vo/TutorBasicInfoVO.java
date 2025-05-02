package com.tutoring.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TutorBasicInfoVO {
    private Long tutorId;
    private String tutorNickname;
    private String tutorEmail;
}
