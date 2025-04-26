package com.tutoring.dto;


import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MeetingBookingDecisionDTO {
    @NotNull private Long bookingId;
    private String comment;
}
