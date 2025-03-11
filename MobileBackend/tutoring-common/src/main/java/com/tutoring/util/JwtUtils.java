package com.tutoring.util;

import com.tutoring.entity.User;
import com.tutoring.enumeration.ErrorCode;
import com.tutoring.exception.CustomException;
import io.jsonwebtoken.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 基于 io.jsonwebtoken 0.9.1 的示例
 */
@Component
@Data
public class JwtUtils {

    @Value("${jwt.secret:defaultSecretKey}")
    private String secretKey;

    // 默认1天(毫秒)
    @Value("${jwt.expiration:86400000}")
    private long expiration;

    /**
     * 生成 JWT Token
     *  - subject 存放 userId
     *  - role 存放 user.getRole().name()
     *  - email
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(String.valueOf(user.getUserId()))
                .claim("role", user.getRole().name())
                .claim("email", user.getEmail())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * 检查 Token 是否有效（签名是否正确 & 是否过期）
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * 获取 Claims
     */
    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}