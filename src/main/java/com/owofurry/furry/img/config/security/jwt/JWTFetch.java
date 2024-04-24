package com.owofurry.furry.img.config.security.jwt;

import jakarta.servlet.http.HttpServletRequest;

public interface JWTFetch {
    static JWTFetch getInstance() {
        return request -> request.getHeader("Auth-Token");
    }

    String getJWTStr(HttpServletRequest request);
}
