package com.iteaj.admin;

import com.google.code.kaptcha.Producer;
import com.iteaj.admin.service.AdminUserPasswordRealm;
import com.iteaj.framework.captcha.KaptchaCreator;
import com.iteaj.framework.spring.boot.config.FrameworkProperties;
import com.iteaj.izone.spi.AuthorizingService;
import com.iteaj.izone.spi.AuthzUrl;
import com.iteaj.izone.web.shiro.OrderShiroFilterChainDefinition;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

/**
 * create time: 2020/3/29
 *
 * @author iteaj
 * @since 1.0
 */
@Configuration
@MapperScan(basePackages = {"com.iteaj.admin.dao"})
public class AdminAuthzConfiguration {

    private final FrameworkProperties properties;
    private final AuthorizingService authorizingService;

    public AdminAuthzConfiguration(FrameworkProperties properties, AuthorizingService authorizingService) {
        this.properties = properties;
        this.authorizingService = authorizingService;
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

    /**
     *  以用户名密码方式登录的权限Realm
     * @return
     */
    @Bean
    public Realm userPasswordRealm(@Qualifier("securityManager") RealmSecurityManager securityManager) {
        AdminUserPasswordRealm userPasswordRealm = new AdminUserPasswordRealm(authorizingService);
        securityManager.setRealm(userPasswordRealm);
        return userPasswordRealm;
    }

    @Bean
    public AuthzUrl shiroAuthUrl() {
        FrameworkProperties.Auth auth = properties.getAuth();
        return new AuthzUrl(auth.getLoginUrl(), auth.getSuccessUrl(), auth.getUnauthorizedUrl());
    }

    /**
     * 字符格式的验证码
     * @return
     */
    @Bean(name = "charCaptcha")
    @ConditionalOnProperty(value = "izb.auth.captcha-type", havingValue = "char")
    public Producer charCaptcha(){
        return KaptchaCreator.charInstance();
    }

    /**
     * 数学计算加、减、乘格式的验证码
     * @return
     */
    @Primary
    @Bean(name = "mathCaptcha")
    @ConditionalOnProperty(value = "izb.auth.captcha-type", havingValue = "math")
    public Producer mathCaptcha(){
        return KaptchaCreator.mathInstance();
    }
}
