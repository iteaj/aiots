package com.iteaj.security.config;

import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.framework.spi.app.AppAuthAction;
import com.iteaj.framework.spi.auth.AppAuthHandler;
import com.iteaj.framework.spring.boot.config.FrameworkProperties;
import com.iteaj.framework.spring.condition.ConditionalOnFramework;
import com.iteaj.framework.spring.condition.Type;
import com.iteaj.security.shiro.AccessFilterWrapper;
import com.iteaj.security.shiro.app.AppAccountAuthAction;
import com.iteaj.security.shiro.app.AppAuthAccessFilter;
import com.iteaj.security.shiro.app.AppAuthenticationRealm;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * create time: 2021/3/23
 *  基于shiro实现的app认证 + session管理
 * @author iteaj
 * @since 1.0
 */
@Configuration
@ConditionalOnFramework(Type.App)
@AutoConfigureBefore(ShiroAutoConfiguration.class)
public class ShiroAppConfiguration {

    private final FrameworkProperties properties;

    public ShiroAppConfiguration(FrameworkProperties properties) {
        this.properties = properties;
    }

    @Bean("appAccessFilter")
    public AccessFilterWrapper appAccessFilter(AppAuthHandler handler) {
        return new AccessFilterWrapper(CoreConst.APP_FILTER_NAME, new AppAuthAccessFilter(handler));
    }

    /**
     * 基于shiro实现的认证动作
     * @return
     */
    @Bean
    @Order(10)
    public AppAuthAction shiroAuthAction() {
        return new AppAccountAuthAction("/app/**");
    }

    /**
     * Shiro对移动端请求进行过滤的url映射处理
     * @see FrameworkProperties.App#getIncludes()
     * @see FrameworkProperties.App#getExcludes()
     * @return
     */
    @Bean
    @Order(value = 80) //
    public OrderFilterChainDefinition appFilterChainDefinition() {
        OrderFilterChainDefinition chain = new OrderFilterChainDefinition();
        // 以下的拦截顺序不能变

        // 可以直接匿名访问的请求
        if(ArrayUtils.isNotEmpty(properties.getApp().getExcludes())) {
            for (String exclude : properties.getApp().getExcludes()) {
                chain.addPathDefinition(exclude, "anon");
            }
        }

        // 必须要认证的请求
        // {@code AppAuthFilter}
        if(ArrayUtils.isNotEmpty(properties.getApp().getIncludes())) {
            for (String include : properties.getApp().getIncludes()) {
                chain.addPathDefinition(include, CoreConst.APP_FILTER_NAME);
            }
        }


        return chain;
    }

    /**
     * 移动端app认证的Realm
     * @return
     */
    @Bean
    public AuthenticatingRealm appAuthenticationRealm() {

        AppAuthenticationRealm appAuthenticationRealm = new AppAuthenticationRealm();

        // 认证信息缓存
//        adminUserPasswordRealm.setAuthenticationCacheName(admin.getAuthCacheName());

        return appAuthenticationRealm;
    }
}
