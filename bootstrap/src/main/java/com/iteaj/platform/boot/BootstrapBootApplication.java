package com.iteaj.platform.boot;

import com.iteaj.izone.spi.IzoneApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@MapperScan(basePackages = {"com.iteaj.admin.dao"})
@SpringBootApplication(scanBasePackages = "com.iteaj.**")
public class BootstrapBootApplication implements IzoneApplication, WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(BootstrapBootApplication.class, args);
    }

}
