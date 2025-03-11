package com.tutoring.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tutoring.entity.User;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserProfileResponse {

    private String name;

    private LocalDate dateOfBirth;

    private String email;

    private String address;

    private User.Role role;

    @JsonProperty("isGoogle")
    private boolean isGoogle;
}
