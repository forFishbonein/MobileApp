package com.tutoring.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentProgressVO {
    private Long   studentId;
    private String nickname;
    private Integer progressPercent;
}
