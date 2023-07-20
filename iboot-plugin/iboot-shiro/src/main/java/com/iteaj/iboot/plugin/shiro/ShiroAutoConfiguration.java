package com.iteaj.iboot.plugin.shiro;

import com.iteaj.framework.autoconfigure.FrameworkProperties;
import com.iteaj.framework.autoconfigure.SessionCacheProperties;
import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.iboot.plugin.shiro.online.OnlineSessionDAO;
import com.iteaj.iboot.plugin.shiro.online.OnlineSessionManager;
import com.iteaj.iboot.plugin.shiro.redis.ShiroRedisSessionCacheManager;
import org.apache.shiro.authc.AbstractAuthenticator;
import org.apache.shiro.authc.AuthenticationListener;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.security.interceptor.AopAllianceAnnotationsAuthorizingMethodInterceptor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.Filter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 使用apache shiro实现的后台权限认证管理
 * create time: 2020/4/2
 * @author iteaj
 * @since 1.0
 */
@ImportAutoConfiguration({ShiroAutoConfiguration.
        RedisSessionCacheManagerConfiguration.class})
@EnableConfigurationProperties(SessionCacheProperties.class)
public class ShiroAutoConfiguration {

    private final FrameworkProperties properties;
    private final SessionCacheProperties cacheProperties;
    @Autowired
    private ObjectProvider<AuthenticationListener> authentication;

    public ShiroAutoConfiguration(FrameworkProperties properties, SessionCacheProperties cacheProperties) {
        this.properties = properties;
        this.cacheProperties = cacheProperties;
    }

    /**
     * shiro实现的用户认证、授权
     * @return
     */
    @Bean(name = {"frameworkAuthRealm", "shiroAdminRealm"})
    public ShiroAdminRealm shiroAdminRealm() {
        return new ShiroAdminRealm();
    }

    /**
     * 创建一个名称叫{@code framework}的shiro全局配置过滤器
     * @return
     */
    @Bean("frameworkAccessFilter")
    public AccessFilterWrapper frameworkAccessFilter() {
        return new AccessFilterWrapper(CoreConst.FRAMEWORK_FILTER_NAME, new ShiroAdminFilter());
    }

    /**
     * 使用框架认证过滤器
     * @return
     */
    @Bean
    @Order(99999)
    public OrderFilterChainDefinition frameworkAuthFilterChainDefinition() {
        OrderFilterChainDefinition chain = new OrderFilterChainDefinition();

        chain.addPathDefinition("/**", CoreConst.FRAMEWORK_FILTER_NAME);
        return chain;
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

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(List<OrderFilterChainDefinition> definitions
            , SecurityManager securityManager, List<AccessFilterWrapper> filters) {

        ShiroFilterFactoryBean factoryBean = new ShiroFilterLogFactoryBean();
        factoryBean.setSecurityManager(securityManager);

        // 处理过滤器
        Map<String, Filter> nameableFilterMap = filters.stream().collect(Collectors
                .toMap(item -> item.getName(), item -> item.getFilter()));
        factoryBean.setFilters(nameableFilterMap);

        // 处理拦截定义
        Map<String, String> pathDefinition = new LinkedHashMap<>();
        definitions.stream().forEach((definition)-> pathDefinition.putAll(definition.getFilterChainMap()));
        factoryBean.setFilterChainDefinitionMap(pathDefinition);

        return factoryBean;
    }

    @Bean
    public SessionDAO sessionDAO() {
        OnlineSessionDAO sessionDAO = new OnlineSessionDAO();
        // 设置缓存名称
        sessionDAO.setActiveSessionsCacheName(cacheProperties.getName());
        return sessionDAO;
    }

    @Bean
    public RealmSecurityManager securityManager(Collection<SessionListener> listeners
            , SessionDAO sessionDAO, Collection<Realm> realms, ObjectProvider<CacheManager> cacheManager) {

        DefaultSessionManager sessionManager = new OnlineSessionManager(properties);
        // 开启session校验
        sessionManager.setSessionValidationSchedulerEnabled(true);

        // 设定全局缓存时间
        long expireTime = TimeUnit.SECONDS.toMillis(cacheProperties.getExpire());
        sessionManager.setGlobalSessionTimeout(expireTime);

        sessionManager.setSessionDAO(sessionDAO);
        sessionManager.setSessionListeners(listeners);

        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealms(realms);
        securityManager.setSessionManager(sessionManager);

        // 设置认证监听器列表
        final Authenticator authenticator = securityManager.getAuthenticator();
        if(authenticator instanceof AbstractAuthenticator) {
            final ArrayList<AuthenticationListener> authenticationListeners =
                    authentication.stream().collect(Collectors.toCollection(ArrayList::new));
            ((AbstractAuthenticator) authenticator).setAuthenticationListeners(authenticationListeners);
        }

        // 如果有缓存管理
        cacheManager.ifAvailable(item -> {
            securityManager.setCacheManager(item);
        });

        return securityManager;
    }

    /**
     * redis session cache session集群配置
     */
    @ConditionalOnProperty(value = "framework.shiro.redis", havingValue = "true")
    public static class RedisSessionCacheManagerConfiguration {

        @Bean
        public CacheManager cacheManager(RedisTemplate redisTemplate, SessionCacheProperties properties) {
            return new ShiroRedisSessionCacheManager(redisTemplate, properties);
        }
    }
}
