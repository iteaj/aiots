package com.iteaj.iboot.boot;

import com.iteaj.framework.autoconfigure.FrameworkWebConfiguration;
import com.iteaj.iboot.msn.core.CoreAutoConfiguration;
import com.iteaj.iboot.msn.iot.IotAutoConfiguration;
import com.iteaj.iboot.msn.lcd.LowCodeGenAutoConfiguration;
import com.iteaj.iboot.msn.quartz.QuartzAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 请不用指定参数{@link SpringBootApplication#scanBasePackages()} <hr>
 *     将在各自的模块启用要扫描的包 {@link ComponentScan#basePackages()}
 * @see CoreAutoConfiguration
 */
@ImportAutoConfiguration({
        IotAutoConfiguration.class, // 物联网模块
        QuartzAutoConfiguration.class, // 定时任务模块
        LowCodeGenAutoConfiguration.class, // 低代码代码生成器模块

        // 以下是IBoot的核心配置(必须启用)
        CoreAutoConfiguration.class, // 系统管理模块
})
@SpringBootApplication()
public class BootstrapBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootstrapBootApplication.class, args);
    }

}
