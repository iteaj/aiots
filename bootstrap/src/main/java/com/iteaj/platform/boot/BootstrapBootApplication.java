package com.iteaj.platform.boot;

import com.iteaj.izone.spi.IzoneApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.iteaj.**")
public class BootstrapBootApplication implements IzoneApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootstrapBootApplication.class, args);
    }
}
