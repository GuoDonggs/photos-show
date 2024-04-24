package com.owofurry.furry.img.config;

import com.owofurry.furry.img.config.security.NoAuthenticationHandler;
import com.owofurry.furry.img.config.security.jwt.JWTAuthenticationFilter;
import com.owofurry.furry.img.entity.User;
import com.owofurry.furry.img.service.UserService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootConfiguration
@EnableMethodSecurity
@EnableConfigurationProperties
public class SecurityBeanConfig {

    @Getter
    @Setter
    String[] permits = {
            "/error/**",
            "/user/login", "/user/register", "/user/reset-password",
            "/image/hot", "/image/list", "/image/search/**",
            "/verify/**", "/admin/login",
            "/keyword/search/**",
            "/v3/api-docs/**", "/swagger-ui/**",
            "/favicon.ico",
            "/lover/num/**",
            "/raw/**"
    };


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           UserDetailsService detailsService) throws Exception {
        http.authorizeHttpRequests(auth ->
                        auth.requestMatchers(permits)
                                .permitAll()
                                .anyRequest().authenticated()
                )
                .addFilter(new JWTAuthenticationFilter(detailsService));
        // jwt 实现无状态，不需要 Session 管理器
        http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.NEVER));
        http.exceptionHandling(e -> e.authenticationEntryPoint(new NoAuthenticationHandler())
                .accessDeniedHandler(new NoAuthenticationHandler()));
        // JWT 下也不需要 csrf 保护
        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public UserDetailsService detailsService(UserService userService) {
        return email -> {
            User user = userService.getUser(email);
            if (user == null || user.isBanned()) {
                return null;
            } else {
                return org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUserEmail())
                        .password(user.getUserId().toString())
                        .roles(user.roles2Array())
                        .build();
            }
        };
    }
}
