package com.iteaj.boot;

import com.iteaj.framework.autoconfigure.FrameworkAutoConfiguration;
import com.iteaj.framework.autoconfigure.FrameworkWebConfiguration;
import com.iteaj.msn.core.CoreAutoConfiguration;
import com.iteaj.msn.dev.DevelopAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ImportAutoConfiguration({
        CoreAutoConfiguration.class, // 系统管理模块
        DevelopAutoConfiguration.class, // 开发模块
        FrameworkWebConfiguration.class // 框架配置
})
@SpringBootApplication
public class BootstrapBootApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(BootstrapBootApplication.class, args);
    }


}
