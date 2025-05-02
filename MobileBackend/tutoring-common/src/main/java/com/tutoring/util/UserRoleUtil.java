package com.tutoring.util;


import com.tutoring.entity.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

public class UserRoleUtil {

    public static List<SimpleGrantedAuthority> buildAuthorities(User.Role role) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
