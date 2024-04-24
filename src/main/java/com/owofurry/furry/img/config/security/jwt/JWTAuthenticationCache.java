package com.owofurry.furry.img.config.security.jwt;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.security.core.Authentication;

import java.util.concurrent.TimeUnit;

/*
 用于缓存认证信息
 */
public interface JWTAuthenticationCache {
    static JWTAuthenticationCache getDefault() {
        return new DefaultJWTAuthenticationCache();
    }

    void delete(String jwt);

    void add(String jwt, Authentication token);

    Authentication get(String jwt);

    /*
    默认实现不推荐使用，推荐redis
     */
    class DefaultJWTAuthenticationCache implements JWTAuthenticationCache {

        Cache<String, Authentication> cache = Caffeine.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).maximumSize(500).build();

        @Override
        public void delete(String jwt) {
            cache.invalidate(jwt);
        }

        @Override
        public void add(String jwt, Authentication token) {
            cache.put(jwt, token);
        }

        @Override
        public Authentication get(String jwt) {
            return cache.getIfPresent(jwt);
        }
    }

}
