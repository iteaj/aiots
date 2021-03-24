package com.iteaj.boot;

import com.iteaj.framework.spring.boot.config.FrameworkAppAutoConfiguration;
import com.iteaj.framework.spring.boot.config.FrameworkWebAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ImportAutoConfiguration({
        FrameworkAppAutoConfiguration.class,
        FrameworkWebAutoConfiguration.class
})
@SpringBootApplication
public class BootstrapBootApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(BootstrapBootApplication.class, args);
    }


}
