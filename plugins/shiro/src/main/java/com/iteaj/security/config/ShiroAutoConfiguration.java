package com.iteaj.security.config;

import com.iteaj.framework.security.AuthzUrl;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.framework.spi.admin.LoggerService;
import com.iteaj.framework.spi.event.Listener;
import com.iteaj.framework.spring.boot.config.FrameworkProperties;
import com.iteaj.security.shiro.*;
import com.iteaj.security.shiro.online.OnlineSessionManager;
import com.iteaj.security.shiro.online.OnlineSessionSessionDAO;
import com.iteaj.security.shiro.online.OnlineSubjectDao;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    private final FrameworkProperties properties;

    public ShiroAutoConfiguration(FrameworkProperties properties) {
        this.properties = properties;
    }


    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(List<OrderFilterChainDefinition> definitions
            , SecurityManager securityManager, List<AccessFilterWrapper> filters, AuthzUrl authzUrl) {

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
        Map<String, Filter> nameableFilterMap = filters.stream().collect(Collectors
                .toMap(item -> item.getName(), item -> item.getFilter()));
        factoryBean.setFilters(nameableFilterMap);

        // 处理拦截定义
        Map<String, String> pathDefinition = new LinkedHashMap<>();
        definitions.stream().forEach((definition)->
                pathDefinition.putAll(definition.getFilterChainMap()));
        factoryBean.setFilterChainDefinitionMap(pathDefinition);
        return factoryBean;
    }

    @Bean
    public SessionDAO sessionDAO() {
        OnlineSessionSessionDAO sessionDAO = new OnlineSessionSessionDAO();
        sessionDAO.setActiveSessionsCacheName(properties.getAdmin().getTokenCacheName());
        return sessionDAO;
    }

    @Bean
    public RealmSecurityManager securityManager(ObjectProvider<CacheManager> provider
            , Collection<SessionListener> listeners, SessionDAO sessionDAO
            , Collection<Realm> realms) {

        DefaultSessionManager sessionManager = new OnlineSessionManager(properties);

        // 开启session校验
        sessionManager.setSessionValidationSchedulerEnabled(true);
        // session校验周期 指定五分钟校验一次
        sessionManager.setSessionValidationInterval(5 * 60 * 1000);

        sessionManager.setSessionDAO(sessionDAO);
        sessionManager.setSessionListeners(listeners);

        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setSessionManager(sessionManager);
        securityManager.setSubjectDAO(new OnlineSubjectDao());
        securityManager.setRealms(realms);

        // 设置缓存管理, 如果允许缓存则委托使用spring缓存
        if(properties.getAdmin() != null & properties.getAdmin().isCacheEnable()) {
            provider.ifAvailable((cacheManager -> {
                securityManager.setCacheManager(new ShiroCacheManagerDelegation(cacheManager));
            }));
        }

        return securityManager;
    }

    /**
     * 后台管理端认证和权限信息Realm
     * @return
     */
    @Bean
    public AuthorizingRealm frameworkAuthRealm() {

        FrameworkProperties.Admin admin = properties.getAdmin();

        ShiroFrameworkAuthRealm authRealm = new ShiroFrameworkAuthRealm();

        // 权限信息缓存
        authRealm.setAuthorizationCacheName(admin.getAuthorCacheName());

        // 认证信息缓存
        authRealm.setAuthenticationCacheName(admin.getAuthCacheName());

        return authRealm;
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
