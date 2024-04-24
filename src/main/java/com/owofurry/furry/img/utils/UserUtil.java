package com.owofurry.furry.img.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtil {
    public static Integer getId() {
        return Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
    }

    public static String getMail() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }
}
