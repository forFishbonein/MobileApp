package com.tutoring.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MeetingBookingRequestDTO {
    @NotNull private Long availabilityId;
    @NotBlank
    private String  content;        // 想讨论的内容
}
