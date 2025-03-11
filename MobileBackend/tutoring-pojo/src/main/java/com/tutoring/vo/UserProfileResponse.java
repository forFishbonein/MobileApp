package com.tutoring.vo;

import com.tutoring.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfileResponse {

    private Long userId;
    private String email;
    private User.Role role;
    private User.AccountStatus accountStatus;
    private Boolean emailVerified;
    private String nickname;
    private String bio;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

