package com.iteaj.framework.spring.boot.config;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.mybatis.MybatisPlusConfiguration;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.framework.spi.app.AppAuthAction;
import com.iteaj.framework.spi.auth.AccountAuthHandler;
import com.iteaj.framework.spi.auth.AppAuthHandler;
import com.iteaj.framework.spi.auth.FrameworkAuthServlet;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * create time: 2021/3/14
 *  核心框架层的配置信息
 * @author iteaj
 * @since 1.0
 */
@ComponentScan({
        "com.iteaj.*.config", // 注入所有模块的自动配置类
        "com.iteaj.*.service.**" // 注入业务层组件
})
@MapperScan(basePackages = {"com.iteaj.*.mapper"}) // 注入dao层组件
@ImportAutoConfiguration(MybatisPlusConfiguration.class)
@EnableConfigurationProperties({FrameworkProperties.class})
public class FrameworkAutoConfiguration {

    private final FrameworkProperties properties;

    public FrameworkAutoConfiguration(FrameworkProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean({CacheManager.class})
    public CacheManager defaultCacheManager() {
        return new ConcurrentMapCacheManager();
    }

    /**
     * 认证授权地址需要不能被拦截
     * 包括登录和注销
     * @see AuthConfig#getLoginUri()
     * @see AuthConfig#getLogoutUri()
     * @return
     */
    @Bean
    @Order(20)
    public OrderFilterChainDefinition authFilterChainDefinition() {
        OrderFilterChainDefinition chain = new OrderFilterChainDefinition();

        String loginUri = properties.getAuth().getLoginUri();
        String logoutUri = properties.getAuth().getLogoutUri();

        // 匿名访问登录和注销地址
        chain.addPathDefinition(loginUri, "anon");
        chain.addPathDefinition(logoutUri, "anon");

        return chain;
    }

    /**
     * 使用框架认证过滤器
     * @return
     */
    @Bean
    @Order(999)
    public OrderFilterChainDefinition frameworkAuthFilterChainDefinition() {
        OrderFilterChainDefinition chain = new OrderFilterChainDefinition();

        chain.addPathDefinition("/**", CoreConst.FRAMEWORK_FILTER_NAME);
        return chain;
    }

    /**
     * 框架的授权认证登录服务
     * @param handler
     * @return
     */
    @Bean
    public ServletRegistrationBean frameworkAuthServlet(AppAuthHandler handler) {
        String redirectUri = properties.getAuth().getLoginUri();
        return new ServletRegistrationBean(new FrameworkAuthServlet(handler), redirectUri);
    }

    /**
     * 使用账号密码认证的处理器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean({AppAuthHandler.class})
    public AppAuthHandler appAuthHandler(List<AppAuthAction> actions) {
        return new AccountAuthHandler(actions);
    }
}
