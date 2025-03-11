package com.tutoring.dto;

import lombok.Data;

@Data
public class UpdateUserProfileRequest {

    private String nickname;


    private String bio;

    // 如果你想直接在更新接口里覆盖 avatarUrl（不通过上传接口）
    // 也可以保留这个字段：
//     private String avatarUrl;
}
