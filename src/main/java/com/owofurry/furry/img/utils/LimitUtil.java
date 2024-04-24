package com.owofurry.furry.img.utils;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class LimitUtil {
    private static RedisTemplate<String, Object> redisTemplate;

    public static void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        LimitUtil.redisTemplate = redisTemplate;
    }

    /**
     * 限流
     *
     * @param key           key
     * @param count         最大次数
     * @param expireMinutes 过期分钟
     * @return boolean true - 到达阈值设置，false 没有达到预设值
     */
    public static boolean cumulative(String key, int count, int expireMinutes) {
        Long increment = redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, expireMinutes, TimeUnit.MINUTES);
        if (increment == null) {
            increment = 0L;
        }
        return increment > count;
    }
}
