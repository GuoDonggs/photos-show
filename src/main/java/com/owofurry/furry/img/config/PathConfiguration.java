package com.owofurry.furry.img.config;

import cn.hutool.core.io.FileUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

@Getter
@Slf4j
@SpringBootConfiguration
@ConfigurationProperties(prefix = "config.path")
public class PathConfiguration implements InitializingBean {
    /**
     * 保存上传文件的根路径
     */
    String base = System.getProperty("user.dir") + File.separator + "data";
    /**
     * 保存上传图像的根路径
     */
    String image = File.separator + "images";
    /**
     * 保存上传视频的根路径
     */
    String video = File.separator + "videos";
    @Setter
    boolean cutFile = false;
    @Setter
    int oneFileSize = 4096;


    public void setBase(String base) {
        this.base = replacePath(base);
    }


    public void setImage(String image) {
        this.image = replacePath(image);
    }

    public void setVideo(String video) {
        this.video = replacePath(video);
    }

    private String replacePath(String path) {
        return path.replace("\\", File.separator).replace("/", File.separator);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 检查路径是否存在，不存在则创建
        File baseFile = new File(base);
        if (!baseFile.exists()) {
            FileUtil.mkdir(baseFile);
        }
        File imageFile = new File(base + image);
        if (!imageFile.exists()) {
            FileUtil.mkdir(imageFile);
        }
        File videoFile = new File(base + video);
        if (!videoFile.exists()) {
            FileUtil.mkdir(videoFile);
        }
        log.info("根路径: {}", baseFile.getAbsolutePath());
        log.info("图片存储路径: {}", baseFile.getAbsolutePath() + File.separator + image);
        log.info("视频存储路径: {}", baseFile.getAbsolutePath() + File.separator + video);
    }
}
