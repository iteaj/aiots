package com.iteaj.framework.spring.boot.config;

import com.iteaj.framework.spi.app.AccountAuthHandler;
import com.iteaj.framework.spi.app.AppAuthAction;
import com.iteaj.framework.spi.app.AppAuthHandler;
import com.iteaj.framework.spi.app.AppLoginServlet;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
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
@EnableConfigurationProperties({FrameworkAppProperties.class})
public class FrameworkAppAutoConfiguration {

    private final FrameworkAppProperties properties;

    public FrameworkAppAutoConfiguration(FrameworkAppProperties properties) {
        this.properties = properties;
    }

    /**
     * app认证处理器, 默认使用账号密码认证
     * @return
     */
    @Bean
    @ConditionalOnMissingBean({AppAuthHandler.class})
    public AppAuthHandler appAuthHandler(List<AppAuthAction> actions) {
        return new AccountAuthHandler(actions);
    }

    @Bean
    public ServletRegistrationBean oath2RedirectServlet(AppAuthHandler handler) {
        String redirectUri = properties.getRedirectUri();
        return new ServletRegistrationBean(new AppLoginServlet(handler), redirectUri);
    }
}
