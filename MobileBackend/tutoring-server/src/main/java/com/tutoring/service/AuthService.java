package com.tutoring.service;

import com.tutoring.dto.ChangePasswordRequest;
import com.tutoring.dto.ForgotPasswordRequest;
import com.tutoring.dto.LoginRequest;
import com.tutoring.dto.ResetPasswordRequest;
import com.tutoring.vo.LoginResponse;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    LoginResponse loginWithGoogle(String googleIdToken);
    public void changePassword(ChangePasswordRequest request);
}

