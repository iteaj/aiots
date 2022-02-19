package com.iteaj.msn.core;

import com.iteaj.framework.autoconfigure.CaptchaAutoConfiguration;
import com.iteaj.framework.autoconfigure.ShiroAutoConfiguration;
import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.logger.LoggerService;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.framework.security.shiro.AccessFilterWrapper;
import com.iteaj.framework.security.shiro.ShiroAuthAccessFilter;
import com.iteaj.framework.spi.admin.DefaultModule;
import com.iteaj.framework.spi.admin.Module;
import com.iteaj.framework.spi.admin.ResourceManager;
import com.iteaj.framework.spi.auth.WebAuthHandler;
import com.iteaj.msn.core.auth.AdminFilter;
import com.iteaj.msn.core.auth.ShiroAdminAuthRealm;
import com.iteaj.msn.core.exception.AsyncExceptionHandler;
import com.iteaj.msn.core.service.AdminLoggerService;
import com.iteaj.msn.core.service.AdminResourceManager;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * create time: 2020/4/25
 *  系统核心模块
 * @author iteaj
 * @since 1.0
 */
@EnableAsync
@ImportAutoConfiguration({
        CaptchaAutoConfiguration.class, // 校验码配置
        ShiroAutoConfiguration.class, // shiro权限配置
})
@EnableConfigurationProperties(CoreProperties.class)
public class CoreAutoConfiguration implements WebMvcConfigurer, AsyncConfigurer {

    private final CoreProperties coreProperties;

    public CoreAutoConfiguration(CoreProperties coreProperties) {
        this.coreProperties = coreProperties;
    }

    /**
     * 声明此模块的信息
     * @return
     */
    @Bean
    public Module coreModule() {
        return DefaultModule.build("core", "系统模块", 10);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Bean
    public ResourceManager menuResourceManager() {
        return new AdminResourceManager();
    }

    /**
     *  开启自动代理
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(DefaultAdvisorAutoProxyCreator.class)
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator =
                new DefaultAdvisorAutoProxyCreator();

        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    /**
     * shiro实现的用户认证、授权
     * @return
     */
    @Bean("frameworkAuthRealm")
    public ShiroAdminAuthRealm shiroAdminAuthRealm() {
        return new ShiroAdminAuthRealm();
    }

    /**
     * 创建一个名称叫{@code framework}的shiro全局配置过滤器
     * @return
     */
    @Bean("frameworkAccessFilter")
    public AccessFilterWrapper frameworkAccessFilter() {
        final AdminFilter adminFilter = new AdminFilter();
        adminFilter.setLoginUrl(coreProperties.getLoginUrl());
        return new AccessFilterWrapper(CoreConst.FRAMEWORK_FILTER_NAME, adminFilter);
    }

    /**
     * 静态资源拦截可以匿名访问, 访问拦截顺序必须靠前
     * @return
     */
    @Bean
    @Order(value = 18) //
    public OrderFilterChainDefinition staticFilterChainDefinition() {
        OrderFilterChainDefinition chain = new OrderFilterChainDefinition();

        // 静态资源这里配置所有请求路径都可以匿名访问
        chain.addPathDefinition("/js/**", "anon");
        chain.addPathDefinition("/css/**", "anon");
        chain.addPathDefinition("/img/**", "anon");
        chain.addPathDefinition("/libs/**", "anon");
        // api文档
        chain.addPathDefinition("/doc/**", "anon");
        chain.addPathDefinition("/favicon.ico", "anon");
        return chain;
    }

    /**
     * 管理端日志记录服务
     * @return
     */
    @Bean
    public LoggerService loggerService() {
        return new AdminLoggerService();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }
}
