package com.tutoring.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学生可预约的老师基础信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TutorBasicInfoVO {
    private Long tutorId;
    private String tutorNickname;
    private String tutorEmail;
}
