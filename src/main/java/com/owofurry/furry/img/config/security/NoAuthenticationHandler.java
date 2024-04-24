package com.owofurry.furry.img.config.security;

import cn.hutool.json.JSONUtil;
import com.owofurry.furry.img.utils.RUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class NoAuthenticationHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            // 返回一个空的响应
            response.setStatus(200);
            response.setContentType("application/json");
            response.getWriter().write(JSONUtil.toJsonStr(RUtil.ok()));
        } else {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(403);
            response.getWriter()
                    .write(JSONUtil.toJsonStr(RUtil.error(RUtil.NO_AUTH_CODE, "未登录")));
        }
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            // 返回一个空的响应
            response.setStatus(200);
            response.setContentType("application/json");
            response.getWriter().write(JSONUtil.toJsonStr(RUtil.ok()));
        } else {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(404);
            response.getWriter()
                    .write(JSONUtil.toJsonStr(RUtil.notFound()));
        }
    }
}
