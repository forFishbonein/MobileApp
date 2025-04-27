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
    /** 0-100 的整数百分比 */
    private Integer progressPercent;
}
