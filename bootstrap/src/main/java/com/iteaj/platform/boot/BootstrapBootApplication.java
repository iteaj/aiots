package com.iteaj.platform.boot;

import com.iteaj.framework.AbstractWebApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.iteaj.**")
public class BootstrapBootApplication implements AbstractWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootstrapBootApplication.class, args);
    }
}
