package com.owofurry.furry.img.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "config.root")
@Data
public class RootUserConfiguration {
    /**
     * root用户密码
     */
    String password = "qwedq23+<sx3P<))_(*IJ";
}
