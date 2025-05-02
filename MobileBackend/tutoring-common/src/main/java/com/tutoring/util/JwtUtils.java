package com.tutoring.util;

import com.tutoring.entity.User;
import com.tutoring.enumeration.ErrorCode;
import com.tutoring.exception.CustomException;
import io.jsonwebtoken.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Data
public class JwtUtils {


    @Value("${jwt.secret:defaultSecretKey}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    public String generateToken(User user) {
        return buildToken(user.getUserId(), user.getRole().name(), expiration, "auth");
    }

    public String generateResetToken(User user, long ttlMillis) {
        return buildToken(user.getUserId(), null, ttlMillis, "reset");
    }

    private String buildToken(Long subjectId, String role, long ttl, String purpose) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + ttl);
        JwtBuilder b = Jwts.builder()
                .setSubject(String.valueOf(subjectId))
                .setIssuedAt(now)
                .setExpiration(exp)
                .claim("purpose", purpose);
        if (role != null) b.claim("role", role);
        return b.signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }
}