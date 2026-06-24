package com.wardrobe.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wardrobe.backend.mapper")
public class OnlineWardrobeBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineWardrobeBackendApplication.class, args);
    }

}
