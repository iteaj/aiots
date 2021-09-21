package com.iteaj;

import com.iteaj.framework.autoconfigure.FrameworkWebConfiguration;
import com.iteaj.msn.core.CoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@ImportAutoConfiguration({
        CoreAutoConfiguration.class, // 系统管理模块
        FrameworkWebConfiguration.class // 框架配置
})
@SpringBootApplication
public class BootstrapBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootstrapBootApplication.class, args);
    }

}
