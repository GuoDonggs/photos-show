package com.owofurry.furry.img.config;

import com.owofurry.furry.img.utils.LimitUtil;
import com.owofurry.furry.img.utils.MailUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootConfiguration
@Import({RedisTemplateConfig.class})
public class UtilsInitConfig {

    @Resource
    JavaMailSender javaMailSender;

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.mail.username}")
    String from;

    @PostConstruct
    public void init() {
        MailUtil.init(javaMailSender, from);
        LimitUtil.setRedisTemplate(redisTemplate);
    }
}
