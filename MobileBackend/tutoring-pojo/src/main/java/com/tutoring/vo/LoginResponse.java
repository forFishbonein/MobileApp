package com.tutoring.vo;

import com.tutoring.entity.User;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LoginResponse {
    private String token;
    private Long userId;
    private User.Role role;
}
