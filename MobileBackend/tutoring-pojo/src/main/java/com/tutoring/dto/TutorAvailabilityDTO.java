package com.tutoring.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TutorAvailabilityDTO {
    @NotEmpty(message = "可用时间段不能为空")
    private List<AvailabilitySlotDTO> availabilitySlots;
}
