package com.iteaj.framework.autoconfigure;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.framework.security.shiro.*;
import com.iteaj.framework.security.shiro.online.OnlineSessionDAO;
import com.iteaj.framework.security.shiro.online.OnlineSessionManager;
import com.iteaj.framework.security.shiro.redis.ShiroRedisSessionCacheManager;
import com.iteaj.framework.spi.auth.WebAuthHandler;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.Filter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 使用apache shiro实现的后台权限认证管理
 * create time: 2020/4/2
 * @author iteaj
 * @since 1.0
 */
@ImportAutoConfiguration(ShiroAuthActionProxy.class)
@EnableConfigurationProperties(SessionCacheProperties.class)
public class ShiroAutoConfiguration {

    private final FrameworkProperties properties;
    private final SessionCacheProperties cacheProperties;

    public ShiroAutoConfiguration(FrameworkProperties properties, SessionCacheProperties cacheProperties) {
        this.properties = properties;
        this.cacheProperties = cacheProperties;
    }

    /**
     * 创建一个名称叫{@code framework}的shiro全局配置过滤器
     * @see WebAuthHandler 扩展shiro的session, 认证, 授权等功能
     * @see com.iteaj.framework.spi.auth.WebAuthAction
     * @see #frameworkAuthFilterChainDefinition() 拦截所有请求 /**
     * @param handler
     * @return
     */
    @Bean("frameworkAccessFilter")
    public AccessFilterWrapper frameworkAccessFilter(WebAuthHandler handler) {
        return new AccessFilterWrapper(CoreConst.FRAMEWORK_FILTER_NAME, new ShiroAuthAccessFilter(handler));
    }

    /**
     * 使用框架认证过滤器
     * @see #frameworkAccessFilter(WebAuthHandler)
     * @return
     */
    @Bean
    @Order(9999)
    public OrderFilterChainDefinition frameworkAuthFilterChainDefinition() {
        OrderFilterChainDefinition chain = new OrderFilterChainDefinition();

        chain.addPathDefinition("/**", CoreConst.FRAMEWORK_FILTER_NAME);
        return chain;
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
        definitions.stream().forEach((definition)->
                pathDefinition.putAll(definition.getFilterChainMap()));
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

        // 如果有缓存管理
        cacheManager.ifAvailable(item -> {
            securityManager.setCacheManager(item);
        });

        return securityManager;
    }

    /**
     * 后台管理端认证和权限信息Realm
     * @return
     */
    @Bean("frameworkAuthRealm")
    public AuthorizingRealm frameworkAuthRealm() {
        return new ShiroFrameworkAuthRealm();
    }

    /**
     * 监听用户上线/离线
     * @return
     */
    @Bean
    public SessionListener eventSessionListener() {
        return new NativeSessionListener();
    }

    @Bean
    @ConditionalOnExpression("${framework.shiro.redis:false}==true")
    public CacheManager cacheManager(RedisTemplate redisTemplate, SessionCacheProperties properties) {
        return new ShiroRedisSessionCacheManager(redisTemplate, properties);
    }
}
