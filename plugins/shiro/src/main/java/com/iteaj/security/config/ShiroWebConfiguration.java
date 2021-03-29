package com.iteaj.security.config;

import com.iteaj.framework.spi.admin.auth.FrameworkAuthService;
import com.iteaj.framework.spi.auth.WebAuthHandler;
import com.iteaj.framework.spi.event.EventPublisher;
import com.iteaj.framework.spi.event.VoidEventPublisher;
import com.iteaj.framework.spring.condition.ConditionalOnFramework;
import com.iteaj.framework.spring.condition.Type;
import com.iteaj.security.shiro.AuthorizingLogMethodInterceptor;
import com.iteaj.security.shiro.ShiroAuthService;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * create time: 2021/3/23
 *  web后台管理端
 * @author iteaj
 * @since 1.0
 */
@Configuration
@ConditionalOnFramework(Type.Pc)
@AutoConfigureBefore(ShiroAutoConfiguration.class)
public class ShiroWebConfiguration {

    /**
     * shiro实现的认证服务
     * @return
     */
    @Bean
    public FrameworkAuthService authenticatingService(WebAuthHandler handler) {
        return new ShiroAuthService(handler);
    }

    /**
     *  开启自动代理
     * @see #authorizationAttributeSourceAdvisor(RealmSecurityManager, ObjectProvider)
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
     * shiro 启用权限注解拦截通知
     * @param securityManager
     * @param provider
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor
    (RealmSecurityManager securityManager , ObjectProvider<EventPublisher> provider) {
        AuthorizationAttributeSourceAdvisor sourceAdvisor
                = new AuthorizationAttributeSourceAdvisor();

        sourceAdvisor.setSecurityManager(securityManager);

        //如果获取不到事件发布者直接使用空事件发布, 不进行任何事件监听处理
        EventPublisher eventPublisher = provider.getIfAvailable(() -> new VoidEventPublisher());
        sourceAdvisor.setAdvice(new AuthorizingLogMethodInterceptor(eventPublisher));

        return sourceAdvisor;
    }
}
