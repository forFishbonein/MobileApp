package com.tutoring.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class GoogleLoginRequest {
    private String idToken;
}
