package com.owofurry.furry.img;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@MapperScan("com.owofurry.furry.img.mapper")
public class ShowFurryImageApplication {

    public static void main(String[] args) {
        initConfig();
        SpringApplication.run(ShowFurryImageApplication.class, args);
    }

    public static void initConfig() {
        File ymlConfig = new File(System.getProperty("user.dir") + File.separator + "config.yml");
        if (!ymlConfig.exists()) {
            try {
                ymlConfig.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
