package com.tutoring.filter;

import com.tutoring.dao.UserDao;
import com.tutoring.entity.User;
import com.tutoring.enumeration.ErrorCode;
import com.tutoring.exception.CustomException;
import com.tutoring.util.JwtUtils;
import com.tutoring.util.UserRoleUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDao userDao;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token != null && jwtUtils.validateToken(token)) {
            Claims claims = jwtUtils.getClaims(token);
            String userIdStr = claims.getSubject();
            String roleStr = (String) claims.get("role");

            try {
                Long userId = Long.valueOf(userIdStr);
                User user = userDao.selectById(userId);
                if (user == null) {
                    throw new CustomException(ErrorCode.UNAUTHORIZED, "User not found.");
                }

                if (user.getRole().name().equals(roleStr)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    UserRoleUtil.buildAuthorities(user.getRole())
                            );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (NumberFormatException e) {
                log.warn("Token subject not a number: {}", userIdStr);
            }
        }

        filterChain.doFilter(request, response);
    }
}
