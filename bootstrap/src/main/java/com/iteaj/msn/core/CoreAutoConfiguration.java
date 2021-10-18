package com.iteaj.msn.core;

import com.iteaj.framework.autoconfigure.CaptchaAutoConfiguration;
import com.iteaj.framework.autoconfigure.ShiroAutoConfiguration;
import com.iteaj.framework.logger.LoggerService;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.framework.spi.admin.DefaultModule;
import com.iteaj.framework.spi.admin.Module;
import com.iteaj.framework.spi.admin.ResourceManager;
import com.iteaj.framework.spi.auth.WebAuthAction;
import com.iteaj.msn.core.auth.AdminLoginAction;
import com.iteaj.msn.core.auth.AdminOAuth2Action;
import com.iteaj.msn.core.service.*;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.spring.security.interceptor.AopAllianceAnnotationsAuthorizingMethodInterceptor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * create time: 2020/4/25
 *  系统核心模块
 * @author iteaj
 * @since 1.0
 */
@ImportAutoConfiguration({
        CaptchaAutoConfiguration.class, // 校验码配置
        ShiroAutoConfiguration.class, // shiro权限配置
})
@MapperScan(basePackages = {"com.iteaj.msn.core.mapper"})
@EnableConfigurationProperties(CoreProperties.class)
public class CoreAutoConfiguration implements WebMvcConfigurer {

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
     * shiro 启用权限注解拦截
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(RealmSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor sourceAdvisor = new AuthorizationAttributeSourceAdvisor();

        sourceAdvisor.setSecurityManager(securityManager);
        sourceAdvisor.setAdvice(new AopAllianceAnnotationsAuthorizingMethodInterceptor());

        return sourceAdvisor;
    }

    /**
     * 后台管理端授权认证动作
     * @return
     */
    @Bean
    @Order(99999)
    public WebAuthAction adminAuthAction() {
        final String loginUrl = coreProperties.getLoginUrl();
        return new AdminLoginAction("/**", loginUrl);
    }

    /**
     * @see #adminAuthAction() Order(80) 要小于 Order(100)
     * @return
     */
    @Bean
    @Order(168)
    public AdminOAuth2Action oAuth2Action() {
        return new AdminOAuth2Action(new String[]{"/core/oauth2"});
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

}
