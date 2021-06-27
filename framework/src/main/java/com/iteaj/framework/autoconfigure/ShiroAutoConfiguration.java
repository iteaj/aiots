package com.iteaj.framework.autoconfigure;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.framework.shiro.*;
import com.iteaj.framework.shiro.online.OnlineSessionManager;
import com.iteaj.framework.shiro.online.OnlineSessionSessionDAO;
import com.iteaj.framework.spi.admin.LoggerService;
import com.iteaj.framework.spi.auth.WebAuthHandler;
import com.iteaj.framework.spi.event.EventPublisher;
import com.iteaj.framework.spi.event.FrameworkListener;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

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

        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
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
        return new OnlineSessionSessionDAO();
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
        securityManager.setRealms(realms);

        return securityManager;
    }

    /**
     * 后台管理端认证和权限信息Realm
     * @return
     */
    @Bean
    public AuthorizingRealm frameworkAuthRealm() {

        ShiroFrameworkAuthRealm authRealm = new ShiroFrameworkAuthRealm();

        // 关闭缓存
        authRealm.setAuthorizationCachingEnabled(false);
        authRealm.setAuthenticationCachingEnabled(false);
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
    public FrameworkListener authorLogListener(LoggerService loggerService) {
        return new ShiroAuthorLogListener(loggerService);
    }

}
