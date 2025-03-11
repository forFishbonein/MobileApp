package com.tutoring.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutoring.dao.UserDao;
import com.tutoring.dto.LoginRequest;
import com.tutoring.dto.RegisterRequest;
import com.tutoring.entity.User;
import com.tutoring.service.MailService;
import com.tutoring.service.UserService;
import com.tutoring.util.JwtUtils;
import com.tutoring.vo.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    @Autowired
    private MailService mailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 内存Map：只保存【email -> 验证码】, 不需要存储密码/角色等。
     */
    private final Map<String, String> emailCodeMap = new ConcurrentHashMap<>();

    /**
     * 第一步：发送验证码
     */
    @Override
    public void sendEmailCode(String email) {
        // 1. 检查邮箱是否已注册
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getEmail, email);
        User existing = this.getOne(updateWrapper);
        if (existing != null) {
            throw new RuntimeException("Email is already registered.");
        }
        // 2. 生成随机验证码
        String code = generateRandomCode();

        // 3. 缓存： email -> code
        emailCodeMap.put(email, code);

        // 4. 发邮件
        mailService.sendVerificationCode(email, code);

        log.info("Verification code sent, email={}, code={}", email, code);
    }

    /**
     * 第二步：验证验证码 + 注册用户
     */
    @Override
    public User registerWithCode(RegisterRequest request) {
        String email = request.getEmail();
        String inputCode = request.getCode();

        // 1. 从Map中取出验证码
        String storedCode = emailCodeMap.get(email);
        if (storedCode == null) {
            throw new RuntimeException("Verification code expired or not found. Please get a new code.");
        }
        // 2. 对比
        if (!storedCode.equals(inputCode)) {
            throw new RuntimeException("Invalid verification code. Please try again.");
        }

        // 3. 验证角色
        if (!("student".equalsIgnoreCase(request.getRole()) || "tutor".equalsIgnoreCase(request.getRole()))) {
            throw new RuntimeException("Invalid role, must be 'student' or 'tutor'.");
        }

        // 4. 正式注册：插入数据库
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newUser.setRole("tutor".equalsIgnoreCase(request.getRole()) ? User.Role.tutor : User.Role.student);
        newUser.setAccountStatus(User.AccountStatus.Active);
        newUser.setEmailVerified(true);

        this.save(newUser);

        // 5. 注册成功后移除验证码记录
        emailCodeMap.remove(email);

        return newUser;
    }

    /**
     * 登录
     */
    @Override
    public LoginResponse login(LoginRequest loginReq) {
        // 1. 先根据 email 查用户
        User user = this.lambdaQuery()
                .eq(User::getEmail, loginReq.getEmail())
                .one();
        if (user == null) {
            throw new RuntimeException("Incorrect email or password.");
        }
        if (user == null) {
            throw new RuntimeException("Incorrect email or password.");
        }
        // 校验密码
        if (!passwordEncoder.matches(loginReq.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Incorrect email or password.");
        }
        // 检查账号状态
        if (user.getAccountStatus() == User.AccountStatus.Suspended) {
            throw new RuntimeException("Your account is suspended.");
        }
        // 生成JWT
        String token = jwtUtils.generateToken(user);

        // 返回响应
        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.setUserId(user.getUserId());
        resp.setRole(user.getRole());
        return resp;
    }

    // 简易生成六位随机数字验证码
    private String generateRandomCode() {
        int r = (int)((Math.random() * 9 + 1) * 100000);
        return String.valueOf(r);
    }
}
