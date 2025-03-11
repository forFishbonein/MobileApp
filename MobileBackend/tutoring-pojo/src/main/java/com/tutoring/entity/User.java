package com.tutoring.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@TableName("users")
public class User implements Serializable {

    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @TableField("email")
    private String email;

    @TableField("password_hash")
    private String passwordHash;

    @TableField("role")
    private Role role;

    @TableField("account_status")
    private AccountStatus accountStatus;

    @TableField("email_verified")
    private Boolean emailVerified;

    @TableField("nickname")
    private String nickname;

    @TableField("bio")
    private String bio;

    @TableField("avatar_url")
    private String avatarUrl;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 枚举对应数据库的 ENUM('student','tutor')
     */
    public enum Role {
        student, tutor
    }

    /**
     * 枚举对应数据库的 ENUM('Pending','Active','Suspended')
     */
    public enum AccountStatus {
        Pending, Active, Suspended
    }
}
