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

    void sendEmailCode(String email);

    User registerWithCode(RegisterRequest request);

    LoginResponse login(LoginRequest loginReq);

    User getUserProfile(Long userId);

    void updateUserProfile(Long userId, UpdateUserProfileRequest request);

    String uploadAvatar(Long userId, MultipartFile file);

    void sendResetToken(String email);

    void resetPasswordWithToken(ResetPasswordJwtRequest req);
}
