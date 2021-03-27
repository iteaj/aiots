package com.iteaj.framework.spring.boot.config;

import com.iteaj.framework.spi.auth.AccountAuthHandler;
import com.iteaj.framework.spi.app.AppAuthAction;
import com.iteaj.framework.spi.auth.AppAuthHandler;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

/**
 * create time: 2021/3/21
 *  app端的配置信息
 * @author iteaj
 * @since 1.0
 */
@ComponentScan({
        "com.iteaj.*.app.**", // 注入所有app相关组件
})
@ImportAutoConfiguration({
        FrameworkAutoConfiguration.class
})
public class FrameworkAppConfiguration {

    /**
     * app认证处理器, 默认使用账号密码认证
     * @return
     */
    @Bean
    @ConditionalOnMissingBean({AppAuthHandler.class})
    public AppAuthHandler appAuthHandler(List<AppAuthAction> actions) {
        return new AccountAuthHandler(actions);
    }

}
