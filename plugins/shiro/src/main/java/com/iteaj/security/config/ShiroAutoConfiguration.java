package com.iteaj.security.config;

import com.iteaj.framework.authority.OrderFilterChainDefinition;
import com.iteaj.framework.security.AuthzUrl;
import com.iteaj.framework.spi.admin.LoggerService;
import com.iteaj.framework.spi.admin.auth.AuthenticatingService;
import com.iteaj.framework.spi.event.EventPublisher;
import com.iteaj.framework.spi.event.Listener;
import com.iteaj.framework.spi.event.VoidEventPublisher;
import com.iteaj.framework.spring.boot.config.FrameworkWebProperties;
import com.iteaj.security.shiro.*;
import com.iteaj.security.shiro.online.OnlineSessionManager;
import com.iteaj.security.shiro.online.OnlineSessionSessionDAO;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.apache.shiro.web.servlet.NameableFilter;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;

import javax.servlet.Filter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 使用apache shiro实现的后台权限认证管理
 * create time: 2020/4/2
 * @author iteaj
 * @since 1.0
 */
@Configuration
public class ShiroAutoConfiguration {

    private FrameworkWebProperties properties;

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(List<OrderFilterChainDefinition> definitions
            , SecurityManager securityManager, List<NameableFilter> filters, AuthzUrl authzUrl) {

        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean() {
            @Override
            protected AbstractShiroFilter createInstance() throws Exception {
                AbstractShiroFilter instance = super.createInstance();
                return new SpringShiroFilter(instance.getSecurityManager(), instance.getFilterChainResolver());
            }
            class SpringShiroFilter extends AbstractShiroFilter {

                protected SpringShiroFilter(WebSecurityManager webSecurityManager, FilterChainResolver resolver) {
                    super();
                    if (webSecurityManager == null) {
                        throw new IllegalArgumentException("WebSecurityManager property cannot be null.");
                    }
                    setSecurityManager(webSecurityManager);
                    if (resolver != null) {
                        setFilterChainResolver(resolver);
                    }

                    setStaticSecurityManagerEnabled(true);
                }
            }

            @Override
            public Class getObjectType() {
                return SpringShiroFilter.class;
            }
        };
        factoryBean.setSecurityManager(securityManager);

        // 权限跳转地址配置
        factoryBean.setLoginUrl(authzUrl.getLoginUrl());
        factoryBean.setSuccessUrl(authzUrl.getSuccessUrl());
        factoryBean.setUnauthorizedUrl(authzUrl.getUnauthorizedUrl());

        // 处理过滤器
        Map<String, Filter> nameableFilterMap = filters.stream()
                .collect(Collectors.toMap(item -> item.getFilterConfig().getFilterName(), item -> item));
        factoryBean.setFilters(nameableFilterMap);

        // 处理拦截定义
        Map<String, String> pathDefinition = new LinkedHashMap<>();
        definitions.stream().forEach((definition)->pathDefinition.putAll(definition.getFilterChainMap()));
        factoryBean.setFilterChainDefinitionMap(pathDefinition);
        return factoryBean;
    }

    @Bean("ajaxFilter")
    @ConditionalOnMissingBean
    public NameableFilter AjaxFilter() {
        return new AjaxFilter();
    }

    /**
     * Shiro权限认证的静态资源拦截
     * 静态资源的访问拦截顺序必须靠前
     * @return
     */
    @Bean
    @Order(value = 8) //
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

    @Bean
    public SessionDAO sessionDAO() {
        return new OnlineSessionSessionDAO();
    }

    @Bean
    public RealmSecurityManager securityManager(ObjectProvider<CacheManager> provider
            , Collection<SessionListener> listeners, ObjectProvider<TaskScheduler> executors
            , SessionDAO sessionDAO, FrameworkWebProperties frameworkWebProperties) {
        DefaultWebSessionManager sessionManager = new OnlineSessionManager
                (executors.getIfAvailable(), frameworkWebProperties);
        DefaultWebSecurityManager webSecurityManager = new DefaultWebSecurityManager();

        // 设置缓存管理， 委托使用spring缓存
        provider.ifAvailable((cacheManager -> {
            webSecurityManager.setCacheManager(new ShiroCacheManagerDelegation(cacheManager));
        }));

        // 开启session校验
        sessionManager.setSessionValidationSchedulerEnabled(true);
        // session校验周期 指定十分钟校验一次
        sessionManager.setSessionValidationInterval(10 * 60 * 1000);

        sessionManager.setSessionDAO(sessionDAO);
        sessionManager.setSessionListeners(listeners);
        webSecurityManager.setSessionManager(sessionManager);
        return webSecurityManager;
    }

    /**
     * shiro实现的认证服务
     * @return
     */
    @Bean
    public AuthenticatingService authenticatingService() {
        return new ShiroAuthenticatingService();
    }

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
            (@Qualifier("securityManager") RealmSecurityManager securityManager
                    , ObjectProvider<EventPublisher> provider) {
        AuthorizationAttributeSourceAdvisor sourceAdvisor
                = new AuthorizationAttributeSourceAdvisor();

        sourceAdvisor.setSecurityManager(securityManager);

        //如果获取不到事件发布者直接使用空事件发布, 不进行任何事件监听处理
        EventPublisher eventPublisher = provider.getIfAvailable(() -> new VoidEventPublisher());
        sourceAdvisor.setAdvice(new AuthorizingLogMethodInterceptor(eventPublisher));

        return sourceAdvisor;
    }

    /**
     * 监听用户上线/离线
     * @return
     */
    @Bean
    public SessionListener eventSessionListener() {
        return new NativeSessionListener();
    }

    /**
     * shiro权限监听器, 用于记录访问日志
     * @return
     */
    @Bean
    @ConditionalOnBean(LoggerService.class)
    public Listener authorLogListener(LoggerService loggerService) {
        return new ShiroAuthorLogListener(loggerService);
    }

}
