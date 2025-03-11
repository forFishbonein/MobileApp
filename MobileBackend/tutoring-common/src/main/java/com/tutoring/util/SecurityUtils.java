package com.tutoring.util;

import com.tutoring.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public final class SecurityUtils {

    // Private constructor to prevent instantiation
    private SecurityUtils() { }

    /**
     * Retrieves the current logged-in user's ID from the SecurityContext.
     * <p>
     * This method extracts the authentication principal from the SecurityContextHolder and
     * returns the user ID if the principal is an instance of {@link User}.
     * </p>
     *
     * @return the current user ID, or {@code null} if not authenticated or an error occurs.
     */
    public static Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                return user.getUserId();
            }
        } catch (Exception e) {
            log.error("Error retrieving current user ID: ", e);
        }
        return null;
    }

    /**
     * 获取当前登录用户角色
     */
    public static User.Role getCurrentUserRole() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                return user.getRole();
            }
        } catch (Exception e) {
            log.error("Error retrieving current user role: ", e);
        }
        return null;
    }

    /**
     * 获取当前登录的 User 对象
     */
    public static User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                return (User) authentication.getPrincipal();
            }
        } catch (Exception e) {
            log.error("Error retrieving current user: ", e);
        }
        return null;
    }
}