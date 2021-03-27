package com.iteaj.security.config;

import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.framework.spi.admin.auth.AuthenticatingService;
import com.iteaj.framework.spi.admin.auth.AuthorizingService;
import com.iteaj.framework.spi.UserAccountService;
import com.iteaj.framework.spi.event.EventPublisher;
import com.iteaj.framework.spi.event.VoidEventPublisher;
import com.iteaj.framework.spring.boot.config.FrameworkProperties;
import com.iteaj.framework.spring.condition.ConditionalOnFramework;
import com.iteaj.framework.spring.condition.Type;
import com.iteaj.security.shiro.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    private final FrameworkProperties properties;

    public ShiroWebConfiguration(FrameworkProperties properties) {
        this.properties = properties;
    }

    /**
     * 后台管理端认证和权限信息Realm
     * @param eventPublisher
     * @param authorizingService
     * @param userAccountService
     * @return
     */
    @Bean
    public AuthorizingRealm authorizingRealm(EventPublisher eventPublisher
            , AuthorizingService authorizingService, UserAccountService userAccountService) {

        FrameworkProperties.Admin admin = properties.getAdmin();

        AdminUserPasswordRealm adminUserPasswordRealm = new AdminUserPasswordRealm(
                eventPublisher, properties, authorizingService, userAccountService);

        // 权限信息缓存
        adminUserPasswordRealm.setAuthorizationCacheName(admin.getAuthorCacheName());

        // 认证信息缓存
        adminUserPasswordRealm.setAuthenticationCacheName(admin.getAuthCacheName());

        return adminUserPasswordRealm;
    }

    @Bean
    public ShiroAdminAuthenticationController shiroAdminAuthenticationController() {
        return new ShiroAdminAuthenticationController();
    }

    /**
     * shiro实现的认证服务
     * @return
     */
    @Bean
    public AuthenticatingService authenticatingService() {
        return new ShiroAuthenticatingService();
    }

    /**
     * Shiro权限认证的静态资源拦截
     * 静态资源的访问拦截顺序必须靠前
     * @return
     */
    @Bean
    @Order(value = 100) //
    public OrderFilterChainDefinition staticFilterChainDefinition() {
        OrderFilterChainDefinition chain = new OrderFilterChainDefinition();

        // 静态资源这里配置所有请求路径都可以匿名访问
        chain.addPathDefinition("/js/**", "anon");
        chain.addPathDefinition("/css/**", "anon");
        chain.addPathDefinition("/img/**", "anon");
        chain.addPathDefinition("/libs/**", "anon");

        chain.addPathDefinition("/login", "anon");
        chain.addPathDefinition("/captcha", "anon");
        return chain;
    }

    @Order(value = 999)
    @Bean("allFilterChainDefinition")
    @ConditionalOnMissingBean(name = {"allFilterChainDefinition"})
    public OrderFilterChainDefinition allFilterChainDefinition() {
        OrderFilterChainDefinition chain = new OrderFilterChainDefinition();

        chain.addPathDefinition("/**", "ajax,user");
        return chain;
    }

    @Bean("ajaxAccessFilter")
    public AccessFilterWrapper ajaxAccessFilter() {
        return new AccessFilterWrapper("ajax", new AccessControlFilter() {

            private boolean isAjax = false;

            @Override
            protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
                try {
                    if(isLoginRequest(request, response)) return true;
                    if(SecurityUtils.getSubject().isAuthenticated()) return true;

                    // 验证是否是Ajax请求
                    if(request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
                        String ajaxId = ((HttpServletRequest) request).getHeader("x-requested-with");
                        if(ajaxId != null && ajaxId.equalsIgnoreCase("XMLHttpRequest")) {

                            this.isAjax = true;
                            return false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }

            @Override
            protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
                if(isAjax) {
                    ((HttpServletResponse) response)
                            .sendError(HttpServletResponse.SC_UNAUTHORIZED, getLoginUrl());

                    return isAjax = false;
                } else {
                    return true;
                }
            }
        });
    }

    /**
     *
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
