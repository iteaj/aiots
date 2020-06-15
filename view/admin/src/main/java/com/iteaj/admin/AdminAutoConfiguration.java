package com.iteaj.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteaj.admin.interceptor.DemoEnvInterceptor;
import com.iteaj.admin.service.AdminLoggerService;
import com.iteaj.admin.service.IMenuService;
import com.iteaj.admin.service.IOperaLogService;
import com.iteaj.izone.api.LoggerService;
import com.iteaj.izone.spi.event.EventPublisher;
import com.iteaj.izone.spi.event.Listener;
import com.iteaj.izone.web.shiro.AuthorizingLogMethodInterceptor;
import com.iteaj.izone.web.shiro.ShiroAuthorLogListener;
import com.iteaj.izone.web.shiro.ShiroAutoConfiguration;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * create time: 2020/4/25
 *
 * @author iteaj
 * @since 1.0
 */
@Configuration
@ImportAutoConfiguration(ShiroAutoConfiguration.class)
public class AdminAutoConfiguration implements WebMvcConfigurer {

    private final ObjectProvider<DemoEnvInterceptor> provider;

    public AdminAutoConfiguration(ObjectProvider<DemoEnvInterceptor> provider) {
        this.provider = provider;
    }

    /**
     * 主页解析
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/main").setViewName("/main");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        provider.ifAvailable(item->{
            registry.addInterceptor(item).addPathPatterns("/**");
        });
    }

    /**
     * 演示环境拦截器
     * @return
     */
    @Bean
    @ConditionalOnProperty(name = {"izb.env.demon"}, havingValue = "true")
    public DemoEnvInterceptor demoHandlerInterceptor(ObjectMapper objectMapper, IMenuService menuService) {
        return new DemoEnvInterceptor(objectMapper, menuService);
    }

    @Bean
    @ConditionalOnMissingBean(TaskExecutor.class)
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setKeepAliveSeconds(600);
        return taskExecutor;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor
            (@Qualifier("securityManager") RealmSecurityManager securityManager
                    , EventPublisher eventPublisher) {
        AuthorizationAttributeSourceAdvisor sourceAdvisor
                = new AuthorizationAttributeSourceAdvisor();

        sourceAdvisor.setSecurityManager(securityManager);
        sourceAdvisor.setAdvice(new AuthorizingLogMethodInterceptor(eventPublisher));
        return sourceAdvisor;
    }

    @Bean
    public LoggerService loggerService(IOperaLogService logService, IMenuService menuService) {
        return new AdminLoggerService(logService, menuService);
    }

    /**
     * shiro权限监听器, 用于记录访问日志
     * @return
     */
    @Bean
    public Listener authorLogListener(LoggerService loggerService) {
        return new ShiroAuthorLogListener(loggerService);
    }

}
