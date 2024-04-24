package com.owofurry.furry.img.config.security.jwt;


import com.owofurry.furry.img.utils.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class JWTAuthenticationProvider implements AuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(JWTAuthenticationProvider.class);
    UserDetailsService userDetailsService;

    public JWTAuthenticationProvider(UserDetailsService detailsService) {
        this.userDetailsService = detailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        assert authentication instanceof JWTAuthenticationToken;
        JWTAuthenticationToken loginAuthentication = (JWTAuthenticationToken) authentication;
        String jwtStr = loginAuthentication.getJwt();
        // 对 jwt 进行验证，判断 jwt 是否过时以及被篡改
        if (!JWTUtil.verify(jwtStr)) {
            throw new BadCredentialsException("未登录");
        }
        // 获取传入的 host
        String provideHost = loginAuthentication.getHost();
        // 解码jwt
        JWTAuthenticationToken decodeJwtToken = JWTUtil.decode(jwtStr);
        String jwtHost = decodeJwtToken.getHost().replace("null", "");
        if (!jwtHost.isEmpty() && !jwtHost.equals(provideHost)) {
            throw new BadCredentialsException("未登录");
        }
        // 调用 userDetailsService 检验用户是否存在，判断是否为伪造的
        UserDetails userDetails = userDetailsService.loadUserByUsername(decodeJwtToken.getPrincipal());
        if (userDetails != null && userDetails.getUsername() != null) {
            return new JWTAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        } else {
            throw new UsernameNotFoundException("未登录");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JWTAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
