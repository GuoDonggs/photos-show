package com.owofurry.furry.img.config;

import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@SpringBootConfiguration
@ConfigurationProperties(prefix = "config.system")
public class SystemConfiguration {
    /**
     * 最大文件大小,单位 MB
     */
    int maxFileSize = 5;

    /**
     * 单次请求中的最大文件数
     * default 5
     */
    int maxFileNum = 5;

    /**
     * 显示未检查的文件信息
     * default:  true
     */
    boolean showNoCheck = true;

}
