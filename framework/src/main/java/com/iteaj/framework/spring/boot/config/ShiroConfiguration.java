package com.iteaj.framework.spring.boot.config;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.iteaj.framework.api.AuthorizingService;
import com.iteaj.framework.shiro.AllFilter;
import com.iteaj.framework.shiro.OrderShiroFilterChainDefinition;
import com.iteaj.framework.shiro.ShiroAccessFilter;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ConditionalOnWebApplication
public class ShiroConfiguration {

    private final FrameworkProperties frameworkProperties;
    @SofaReference(binding = @SofaReferenceBinding(bindingType = "${bind.type}"))
    private AuthorizingService authorizingService;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public ShiroConfiguration(FrameworkProperties frameworkProperties) {
        this.frameworkProperties = frameworkProperties;
    }

    @Bean
    public WebSecurityManager webSecurityManager(Realm realm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        return securityManager;
    }

    /**
     * 配置Shiro对静态资源的拦截为匿名
     * @return
     */
    @Order(value = 8) //静态资源的访问拦截顺序必须靠前
    @Bean("StaticFilterChainDefinition")
    @ConditionalOnBean(WebSecurityManager.class)
    public OrderShiroFilterChainDefinition staticFilterChainDefinition() {
        OrderShiroFilterChainDefinition chain = new OrderShiroFilterChainDefinition();
        // 静态资源这里配置所有请求路径都可以匿名访问
        chain.addPathDefinition("/js/**", "anon");
        chain.addPathDefinition("/css/**", "anon");
        chain.addPathDefinition("/img/**", "anon");
        chain.addPathDefinition("/libs/**", "anon");
        return chain;
    }

    /**
     * Shiro权限认证的主路径拦截声明
     * @return
     */
    @Bean
    @Order(value = 88) //用来声明权限路径的拦截
    public OrderShiroFilterChainDefinition adminFilterChainDefinition() {
        OrderShiroFilterChainDefinition chain = new OrderShiroFilterChainDefinition();

        chain.addPathDefinition("/login", "anon");
        chain.addPathDefinition("/captcha", "anon");
        return chain;
    }

    @Order(value = 8888)
    @Bean("AllFilterChainDefinition")
    @ConditionalOnBean(WebSecurityManager.class)
    public OrderShiroFilterChainDefinition allFilterChainDefinition() {
        OrderShiroFilterChainDefinition chain = new OrderShiroFilterChainDefinition();

        // Url的拦截是有顺序的, 所以 /** 必须放在最后拦截
        chain.addPathDefinition("/**", "all");
        return chain;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(WebSecurityManager.class)
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager
            ,List<ShiroFilterChainDefinition> definitions, List<ShiroAccessFilter> filters) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);

        // 配置权限管理 路径配置
        if(frameworkProperties.getAuth() == null)
            logger.warn("Shiro权限管理需要声明一些配置：{}.Shiro", FrameworkProperties.class.getName());
        else BeanUtils.copyProperties(frameworkProperties.getAuth(), factoryBean);

        Map<String, String> pathDefinition = new LinkedHashMap<>();
        definitions.stream().forEach((definition)->pathDefinition.putAll(definition.getFilterChainMap()));
        factoryBean.setFilterChainDefinitionMap(pathDefinition);

        // 配置Shiro权限管理 路径过滤器
        factoryBean.setFilters(new HashMap<>());
        if(!CollectionUtils.isEmpty(filters)) {
            filters.stream().forEach((item) ->
                    factoryBean.getFilters().put(item.getName(), item));
        }

        factoryBean.getFilters().put("all", new AllFilter());
        return factoryBean;
    }

    @Bean
    public AllFilter globalUserFilter() {
        return new AllFilter();
    }
}

