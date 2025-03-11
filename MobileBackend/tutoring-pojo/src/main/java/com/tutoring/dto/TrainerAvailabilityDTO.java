package com.tutoring.dto;


import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TrainerAvailabilityDTO {
    // @NotEmpty(message = "Availability slots cannot be empty")
    private List<AvailabilitySlotDTO> availabilitySlots;
}
