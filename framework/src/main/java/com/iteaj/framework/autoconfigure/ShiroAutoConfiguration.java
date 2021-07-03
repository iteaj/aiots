package com.iteaj.framework.autoconfigure;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.framework.shiro.*;
import com.iteaj.framework.shiro.online.OnlineSessionDAO;
import com.iteaj.framework.shiro.online.OnlineSessionManager;
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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

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
@EnableConfigurationProperties(ShiroCacheProperties.class)
public class ShiroAutoConfiguration {

    private final FrameworkWebProperties properties;

    public ShiroAutoConfiguration(FrameworkWebProperties properties) {
        this.properties = properties;
    }

    /**
     * 创建一个名称叫{@code framework}的shiro访问过滤器
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
        return new OnlineSessionDAO();
    }

    @Bean
    public RealmSecurityManager securityManager(Collection<SessionListener> listeners
            , SessionDAO sessionDAO, Collection<Realm> realms, ObjectProvider<CacheManager> cacheManager) {

        DefaultSessionManager sessionManager = new OnlineSessionManager(properties);

        // 开启session校验
        sessionManager.setSessionValidationSchedulerEnabled(true);
        // session校验周期 指定五分钟校验一次
        sessionManager.setSessionValidationInterval(5 * 60 * 1000);

        sessionManager.setSessionDAO(sessionDAO);
        sessionManager.setSessionListeners(listeners);

        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealms(realms);
        securityManager.setSessionManager(sessionManager);

        // 如果有缓存管理
        cacheManager.ifAvailable(item -> {
            securityManager.setCacheManager(item);

            // 使用缓存过期时间
            // 60分钟校验一次
            sessionManager.setSessionValidationInterval(TimeUnit.MINUTES.toMillis(60));
            sessionManager.setSessionValidationSchedulerEnabled(true);
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

}
