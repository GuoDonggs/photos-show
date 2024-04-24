package com.owofurry.furry.img.config.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
public class JWTAuthenticationToken extends AbstractAuthenticationToken {

    private String jwt;

    private String principal;
    private String credential;

    private String host;


    public JWTAuthenticationToken() {
        super(null);
    }

    /*
     用于校验成功后创建
     */
    public JWTAuthenticationToken(String principal, String credential, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credential = credential;
        super.setAuthenticated(true);
    }

    /*
     用于 JWT 解码成功弄后创建
     */
    public JWTAuthenticationToken(String principal, String credential, String host) {
        super(null);
        this.principal = principal;
        this.credential = credential;
        this.host = host;
    }

    /*
     用于 JwtFilter 中包装 JWT
     */
    public JWTAuthenticationToken(String jwt) {
        super(null);
        this.jwt = jwt;
    }

    @Override
    public String getCredentials() {
        return this.credential;
    }

    @Override
    public String getPrincipal() {
        return this.principal;
    }
}
