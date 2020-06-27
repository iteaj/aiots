package com.iteaj.platform.boot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

//@EnableCaching
@MapperScan(basePackages = {"com.iteaj.*.mapper"})
@SpringBootApplication(scanBasePackages = "com.iteaj.**")
public class BootstrapBootApplication{

    public static void main(String[] args) {
        SpringApplication.run(BootstrapBootApplication.class, args);
    }

}
