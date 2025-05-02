package com.tutoring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutoring.dao.UserDao;
import com.tutoring.dto.LoginRequest;
import com.tutoring.dto.RegisterRequest;
import com.tutoring.dto.ResetPasswordJwtRequest;
import com.tutoring.dto.UpdateUserProfileRequest;
import com.tutoring.entity.TeacherEmail;
import com.tutoring.entity.User;
import com.tutoring.enumeration.ErrorCode;
import com.tutoring.exception.CustomException;
import com.tutoring.service.MailService;
import com.tutoring.service.OssService;
import com.tutoring.service.TeacherEmailService;
import com.tutoring.service.UserService;
import com.tutoring.util.JwtUtils;
import com.tutoring.vo.LoginResponse;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.role;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    @Autowired
    private MailService mailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private OssService ossService;

    @Autowired
    private TeacherEmailService teacherEmailService;

    private final Map<String, String> emailCodeMap = new ConcurrentHashMap<>();


    private static final long RESET_JWT_TTL = 5 * 60 * 1000;

    @Override
    @Transactional
    public void sendResetToken(String email) {
        User user = this.lambdaQuery()
                .select(User::getUserId, User::getEmail, User::getAccountStatus)
                .eq(User::getEmail, email)
                .one();
        if (user == null || user.getAccountStatus() != User.AccountStatus.Active) {
            return;
        }

        String jwt = jwtUtils.generateResetToken(user, RESET_JWT_TTL);

        String subject = "Your Password Reset Token";
        String text = String.format(
                "You requested to reset your password. Please copy the following token into the app within 5 minutes:\n\n%s",
                jwt
        );
        mailService.sendResetLink(email, subject, text);

        log.info("Sent password reset token to {} (ttl={}ms)", email, RESET_JWT_TTL);
    }

    @Override
    @Transactional
    public void resetPasswordWithToken(ResetPasswordJwtRequest req) {
        String token = req.getToken();
        if (!jwtUtils.validateToken(token)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "Invalid or expired reset token.");
        }
        Claims claims = jwtUtils.getClaims(token);
        String purpose = claims.get("purpose", String.class);
        if (!"reset".equals(purpose)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "Invalid reset token.");
        }
        Long userId = Long.parseLong(claims.getSubject());

        User user = this.getById(userId);
        if (user == null || user.getAccountStatus() != User.AccountStatus.Active) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "User not found or not eligible for reset.");
        }

        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        this.updateById(user);

        log.info("Password reset for userId={}", userId);
    }

    @Override
    public void sendEmailCode(String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        User existing = this.getOne(queryWrapper);

        if (existing != null) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "Email is already registered.");
        }

        String code = generateRandomCode();

        emailCodeMap.put(email, code);

        mailService.sendVerificationCode(email, code);

        log.info("Verification code sent, email={}, code={}", email, code);
    }

    @Override
    public User registerWithCode(RegisterRequest request) {
        String email = request.getEmail();
        String inputCode = request.getCode();

        String storedCode = emailCodeMap.get(email);
        if (storedCode == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST,
                    "Verification code expired or not found. Please get a new code.");
        }
        if (!storedCode.equals(inputCode)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "Invalid verification code. Please try again.");
        }

        if (!("student".equalsIgnoreCase(request.getRole()) || "tutor".equalsIgnoreCase(request.getRole()))) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "Invalid role, must be 'student' or 'tutor'.");
        }

        if ("tutor".equalsIgnoreCase(request.getRole())) {
            TeacherEmail teacherEmail = teacherEmailService.getById(email);
            if (teacherEmail == null) {
                throw new CustomException(ErrorCode.BAD_REQUEST, "This email is not authorized for tutor registration.");
            }
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newUser.setRole("tutor".equalsIgnoreCase(request.getRole()) ? User.Role.tutor : User.Role.student);
        newUser.setAccountStatus(User.AccountStatus.Active);
        newUser.setEmailVerified(true);

        this.save(newUser);

        emailCodeMap.remove(email);

        return newUser;
    }

    @Override
    public LoginResponse login(LoginRequest loginReq) {
        User user = this.lambdaQuery()
                .eq(User::getEmail, loginReq.getEmail())
                .one();

        if (user == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "Incorrect email or password.");
        }

        if (!passwordEncoder.matches(loginReq.getPassword(), user.getPasswordHash())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "Incorrect email or password.");
        }

        if (user.getAccountStatus() == User.AccountStatus.Suspended) {
            throw new CustomException(ErrorCode.FORBIDDEN, "Your account is suspended.");
        }

        String token = jwtUtils.generateToken(user);

        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.setUserId(user.getUserId());
        resp.setRole(user.getRole());
        return resp;
    }

    private String generateRandomCode() {
        int r = (int) ((Math.random() * 9 + 1) * 100000);
        return String.valueOf(r);
    }

    @Override
    public User getUserProfile(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new CustomException(ErrorCode.NOT_FOUND, "User not found.");
        }
        return user;
    }

    @Override
    public void updateUserProfile(Long userId, UpdateUserProfileRequest request) {
        User user = this.getById(userId);
        if (user == null) {
            throw new CustomException(ErrorCode.NOT_FOUND, "User not found.");
        }

        user.setNickname(request.getNickname());
        user.setBio(request.getBio());

        this.updateById(user);
    }

    @Override
    public String uploadAvatar(Long userId, MultipartFile file) {
        User user = this.getById(userId);
        if (user == null) {
            throw new CustomException(ErrorCode.NOT_FOUND, "User not found.");
        }

        String avatarUrl = ossService.uploadFile(file);

        user.setAvatarUrl(avatarUrl);
        this.updateById(user);

        return avatarUrl;
    }
}
