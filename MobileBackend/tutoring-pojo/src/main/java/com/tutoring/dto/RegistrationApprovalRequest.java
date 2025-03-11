package com.tutoring.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistrationApprovalRequest {
    @NotBlank(message = "Decision cannot be blank")
    private String decision; // "approved" 或 "rejected"
}
