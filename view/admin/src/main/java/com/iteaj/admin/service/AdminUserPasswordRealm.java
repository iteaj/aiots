package com.iteaj.admin.service;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.captcha.KaptchaCreator;
import com.iteaj.framework.spring.boot.config.FrameworkProperties;
import com.iteaj.framework.utils.ShiroUtils;
import com.iteaj.izone.spi.AuthorizingService;
import com.iteaj.izone.spi.AuthzUrl;
import com.iteaj.izone.spi.event.AsyncPublisher;
import com.iteaj.izone.spi.event.EventPublisher;
import com.iteaj.izone.web.shiro.AuthorizingLogMethodInterceptor;
import com.iteaj.izone.web.shiro.OrderShiroFilterChainDefinition;
import com.iteaj.izone.web.shiro.ShiroAuthToken;
import com.iteaj.izone.web.shiro.ShiroAuthorEvent;
import com.iteaj.util.module.mvc.orm.Entity;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;

import java.util.Objects;

@Configuration
public class AdminUserPasswordRealm extends AuthorizingRealm {

    private FrameworkProperties properties;
    private AuthorizingService authorizingService;

    public AdminUserPasswordRealm(AuthorizingService authorizingService
            , @Qualifier("securityManager") RealmSecurityManager securityManager) {
        this.authorizingService = authorizingService;
        securityManager.setRealm(this);
        Assert.notNull(authorizingService, "未创建授权服务："+AuthorizingService.class);
    }

    @Bean
    public AuthzUrl authzUrl(FrameworkProperties properties) {
        this.properties = properties;
        FrameworkProperties.Auth auth = properties.getAuth();

        return new AuthzUrl(auth.getLoginUrl()
                , auth.getSuccessUrl(), auth.getUnauthorizedUrl());
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

    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor
            (@Qualifier("securityManager") RealmSecurityManager securityManager
                    , ObjectFactory<EventPublisher> objectFactory) {
        AuthorizationAttributeSourceAdvisor sourceAdvisor
                = new AuthorizationAttributeSourceAdvisor();

        EventPublisher publisher = objectFactory.getObject();
        sourceAdvisor.setSecurityManager(securityManager);
        sourceAdvisor.setAdvice(new AuthorizingLogMethodInterceptor(publisher));
        return sourceAdvisor;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setKeepAliveSeconds(600);
        return taskExecutor;
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

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if(!(token instanceof UsernamePasswordToken))
            throw new UnsupportedTokenException("不支持的token");

        Session session = SecurityUtils.getSubject().getSession();
        ShiroAuthToken authToken = (ShiroAuthToken) token;

        if(properties.getAuth().isCaptchaEnabled()) {
            String kaptcha = (String)session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
            if(!Objects.equals(kaptcha, authToken.getCaptcha())) {
                throw new AuthenticationException("验证码错误");
            }
        }

        // 验证账号是否存在
        String username = authToken.getUsername();
        Entity admin = authorizingService.getAdmin(authToken);
        if(null == admin) throw new UnknownAccountException("账号不存在：" + username);

        // 校验密码
        boolean authentication = authorizingService.validCredentials(admin, authToken);
        if(!authentication) throw new CredentialsException("账号密码不匹配");

        String password = new String(((UsernamePasswordToken) token).getPassword());
        return new SimpleAuthenticationInfo(admin, password, getName());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        BaseEntity admin = (BaseEntity)principals.getPrimaryPrincipal();

        // 创建权限对象, 并设置角色和权限信息
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.addRoles(authorizingService.getRoles(admin));
        authorizationInfo.addStringPermissions(authorizingService.getPermissions(admin));

        return authorizationInfo;
    }

    /**
     * 超级管理员包含所有的权限
     * @param principals
     * @param permission
     * @return
     */
    @Override
    public boolean isPermitted(PrincipalCollection principals, String permission) {
        BaseEntity admin = (BaseEntity)principals.getPrimaryPrincipal();
        return ShiroUtils.isSuper(admin.getId()) || super.isPermitted(principals, permission);
    }

    @Override
    public boolean hasRole(PrincipalCollection principal, String roleIdentifier) {
        BaseEntity admin = (BaseEntity)principal.getPrimaryPrincipal();
        return ShiroUtils.isSuper(admin.getId()) || super.hasRole(principal, roleIdentifier);
    }

    @Override
    public void onLogout(PrincipalCollection principals) {
        super.onLogout(principals);
    }
}
