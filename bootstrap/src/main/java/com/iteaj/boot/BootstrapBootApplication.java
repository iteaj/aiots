package com.iteaj.boot;

import com.iteaj.framework.autoconfigure.FrameworkWebConfiguration;
import com.iteaj.msn.core.CoreAutoConfiguration;
import com.iteaj.msn.iot.IotAutoConfiguration;
import com.iteaj.msn.lcd.LowCodeGenAutoConfiguration;
import com.iteaj.msn.quartz.QuartzAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@ImportAutoConfiguration({
        IotAutoConfiguration.class, // 物联网模块
//        QuartzAutoConfiguration.class, // 定时任务模块
        LowCodeGenAutoConfiguration.class, // 低代码代码生成器模块

        CoreAutoConfiguration.class, // 系统管理模块
        FrameworkWebConfiguration.class // 框架Web容器配置
})
@SpringBootApplication()
public class BootstrapBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootstrapBootApplication.class, args);
    }

}
