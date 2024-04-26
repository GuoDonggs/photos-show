package com.owofurry.furry.img;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@MapperScan("com.owofurry.furry.img.mapper")
public class ShowFurryImageApplication {

    public static void main(String[] args) {
       
        SpringApplication.run(ShowFurryImageApplication.class, args);
    }

}
