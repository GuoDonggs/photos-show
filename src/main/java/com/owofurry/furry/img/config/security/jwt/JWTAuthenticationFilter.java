package com.owofurry.furry.img.config.security.jwt;


import com.owofurry.furry.img.config.security.NoOptionAuthenticationHandler;
import com.owofurry.furry.img.utils.RequestAddressUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

/*
JWT 鉴权过滤器
 */

public class JWTAuthenticationFilter extends BasicAuthenticationFilter {


    @Getter
    final private JWTFetch jwtFetch;
    private final JWTAuthenticationCache cache;
    private final AntPathRequestMatcher requestMatcher;
    AuthenticationFailureHandler failureHandler;
    AuthenticationSuccessHandler successHandler;

    public JWTAuthenticationFilter(String defaultFilterProcessesUrl, JWTFetch jwtFetch,
                                   JWTAuthenticationCache cache,
                                   AuthenticationManager authenticationManager,
                                   AuthenticationSuccessHandler successHandler,
                                   AuthenticationFailureHandler failureHandler) {
        super(authenticationManager);
        this.jwtFetch = jwtFetch;
        this.cache = cache;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        requestMatcher = new AntPathRequestMatcher(defaultFilterProcessesUrl);
    }

    public JWTAuthenticationFilter(String defaultFilterProcessesUrl,
                                   JWTFetch jwtFetch,
                                   AuthenticationManager authenticationManager) {
        this(defaultFilterProcessesUrl, jwtFetch, JWTAuthenticationCache.getDefault(), authenticationManager,
                NoOptionAuthenticationHandler.getInstance(), NoOptionAuthenticationHandler.getInstance());
    }

    public JWTAuthenticationFilter(JWTFetch jwtFetch, AuthenticationManager authenticationManager) {
        this("/**", jwtFetch, JWTAuthenticationCache.getDefault(), authenticationManager,
                NoOptionAuthenticationHandler.getInstance(), NoOptionAuthenticationHandler.getInstance());
    }


    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this("/**", JWTFetch.getInstance(), JWTAuthenticationCache.getDefault(), authenticationManager,
                null, null);
    }

    public JWTAuthenticationFilter(UserDetailsService detailsService) {
        this(new ProviderManager(new JWTAuthenticationProvider(detailsService)));
    }

    public JWTAuthenticationFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager) {
        this(defaultFilterProcessesUrl, JWTFetch.getInstance(), JWTAuthenticationCache.getDefault(), authenticationManager,
                NoOptionAuthenticationHandler.getInstance(), NoOptionAuthenticationHandler.getInstance());
    }

    /*
    进行验证
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 判断是否需要进行处理，否则交由后续拦截器判断是否放行
        if (requiresAuthentication(request)) {
            try {
                // 通过 jwt 判断是否通过验证
                Authentication authentication = attemptAuthentication(request, response);
                if (authentication != null) {
                    // 存储 authentication 信息
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    if (authentication.isAuthenticated() && successHandler != null) {
                        successHandler.onAuthenticationSuccess(request, response, authentication);
                    }
                }
            } catch (AuthenticationException e) {
                if (failureHandler != null) {
                    failureHandler.onAuthenticationFailure(request, response, e);
                }
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }

    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException {
        Authentication authenticate;
        // 获取 jwt 信息
        String jwtStr = jwtFetch.getJWTStr(request);
        // 从尝试缓存中获取 jwt 对应的验证信息
        if ((authenticate = cache.get(jwtStr)) != null) {
            return authenticate;
        }
        // 打包
        JWTAuthenticationToken token = new JWTAuthenticationToken(jwtStr);
        token.setHost(RequestAddressUtil.getRemoteAddress(request));
        // 进行验证
        authenticate = getAuthenticationManager().authenticate(token);
        // 通过验证，加入缓存
        if (authenticate.isAuthenticated()) {
            cache.add(jwtStr, authenticate);
        }
        return authenticate;
    }

    /*
    判断是否执行官过滤器，不执行则交由后续过滤器鉴权
     */
    protected boolean requiresAuthentication(HttpServletRequest request) {
        // 判断路径是否匹配同时 jwt 是否为空
        return requestMatcher.matches(request) &&
                jwtFetch.getJWTStr(request) != null;
    }
}
