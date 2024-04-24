package com.owofurry.furry.img.config.security;

import cn.hutool.json.JSONUtil;
import com.owofurry.furry.img.utils.RUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class NoOptionAuthenticationHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    private static final NoOptionAuthenticationHandler handler = new NoOptionAuthenticationHandler();

    private NoOptionAuthenticationHandler() {
    }

    public static NoOptionAuthenticationHandler getInstance() {
        return handler;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            // 返回一个空的响应
            response.setStatus(200);
            response.setContentType("application/json");
            response.getWriter().write(JSONUtil.toJsonStr(RUtil.ok()));
        } else {
            response.setContentType("text/plan");
            response.setCharacterEncoding("utf-8");
            response.setStatus(401);
            response.getWriter().write(JSONUtil.toJsonStr(RUtil.error(RUtil.NO_AUTH_CODE, exception.getMessage())));
        }
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

    }
}
