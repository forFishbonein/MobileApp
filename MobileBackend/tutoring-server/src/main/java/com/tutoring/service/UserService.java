package com.tutoring.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tutoring.dto.LoginRequest;
import com.tutoring.dto.RegisterRequest;
import com.tutoring.dto.ResetPasswordJwtRequest;
import com.tutoring.dto.UpdateUserProfileRequest;
import com.tutoring.entity.User;
import com.tutoring.vo.LoginResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends IService<User> {

    /**
     * 发送邮箱验证码（只传 email）
     */
    void sendEmailCode(String email);

    /**
     * 第二步：校验验证码 + 注册用户
     */
    User registerWithCode(RegisterRequest request);

    /**
     * 登录
     */
    LoginResponse login(LoginRequest loginReq);

    /**
     * 获取指定用户信息
     */
    User getUserProfile(Long userId);

    /**
     * 更新指定用户的个人信息（昵称、bio、以及可能的 avatarUrl）
     */
    void updateUserProfile(Long userId, UpdateUserProfileRequest request);

    /**
     * 上传头像，并返回头像的访问URL
     * （若要立即更新数据库里用户的 avatarUrl，也可在这里一起处理）
     */
    String uploadAvatar(Long userId, MultipartFile file);

    /**
     * 发送重置密码的短期 JWT 令牌到邮箱（有效期 5 分钟）
     */
    void sendResetToken(String email);

    /**
     * 使用 JWT 令牌重置密码
     */
    void resetPasswordWithToken(ResetPasswordJwtRequest req);
}
