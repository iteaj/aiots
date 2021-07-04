package com.iteaj.msn.core;

import com.iteaj.framework.autoconfigure.CaptchaAutoConfiguration;
import com.iteaj.framework.autoconfigure.ShiroAutoConfiguration;
import com.iteaj.framework.logger.LoggerService;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.framework.spi.admin.Module;
import com.iteaj.framework.spi.admin.ResourceManager;
import com.iteaj.framework.spi.admin.event.OnlineListener;
import com.iteaj.framework.spi.admin.event.OnlineType;
import com.iteaj.framework.spi.auth.WebAuthAction;
import com.iteaj.framework.spi.event.FrameworkListener;
import com.iteaj.msn.core.app.OAuth2DemoAction;
import com.iteaj.msn.core.auth.AdminLoginAction;
import com.iteaj.msn.core.auth.AdminOAuth2Action;
import com.iteaj.msn.core.entity.Admin;
import com.iteaj.msn.core.entity.OnlineUser;
import com.iteaj.msn.core.enums.AppType;
import com.iteaj.msn.core.service.AdminLoggerService;
import com.iteaj.msn.core.service.IConfigService;
import com.iteaj.msn.core.service.IOnlineUserService;
import com.iteaj.msn.core.service.MenuResourceManager;
import com.iteaj.oauth2.client.action.OAuth2Action;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.spring.security.interceptor.AopAllianceAnnotationsAuthorizingMethodInterceptor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Date;
import java.util.List;

/**
 * create time: 2020/4/25
 *  系统核心模块
 * @author iteaj
 * @since 1.0
 */
@ImportAutoConfiguration({
        CaptchaAutoConfiguration.class,
        ShiroAutoConfiguration.class,
})
@ComponentScan("com.iteaj.msn.core")
@MapperScan(basePackages = {"com.iteaj.msn.core.mapper"})
@EnableConfigurationProperties(CoreProperties.class)
public class CoreAutoConfiguration implements WebMvcConfigurer{

    private final CoreProperties coreProperties;

    public CoreAutoConfiguration(CoreProperties coreProperties) {
        this.coreProperties = coreProperties;
    }

    @Bean
    @Order(8)
    public OAuth2Action wechatAction() {
        return new OAuth2DemoAction("/app/wechat/**");
    }

    /**
     * 声明此模块的信息
     * @return
     */
    @Bean
    public Module coreModule() {
        return new Module() {
            @Override
            public int getOrder() {
                return 1;
            }

            @Override
            public String getMsn() {
                return "core";
            }

            @Override
            public String getName() {
                return "系统核心";
            }
        };
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Bean
    public ResourceManager menuResourceManager(
            IConfigService configService, List<Module> modules) {
        return new MenuResourceManager(configService, modules);
    }

    /**
     *  开启自动代理
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(DefaultAdvisorAutoProxyCreator.class)
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator =
                new DefaultAdvisorAutoProxyCreator();

        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
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

    /**
     * 后台管理端授权认证动作
     * @return
     */
    @Bean
    @Order(100)
    public WebAuthAction adminAuthAction() {
        final String loginUrl = coreProperties.getLoginUrl();
        return new AdminLoginAction("/core/**", loginUrl);
    }

    /**
     * @see #adminAuthAction() Order(80) 要小于 Order(100)
     * @return
     */
    @Bean
    @Order(80)
    public AdminOAuth2Action oAuth2Action() {
        return new AdminOAuth2Action(new String[]{"/core/oauth2"});
    }

    /**
     * 静态资源拦截可以匿名访问, 访问拦截顺序必须靠前
     * @return
     */
    @Bean
    @Order(value = 18) //
    public OrderFilterChainDefinition staticFilterChainDefinition() {
        OrderFilterChainDefinition chain = new OrderFilterChainDefinition();

        // 静态资源这里配置所有请求路径都可以匿名访问
        chain.addPathDefinition("/js/**", "anon");
        chain.addPathDefinition("/css/**", "anon");
        chain.addPathDefinition("/img/**", "anon");
        chain.addPathDefinition("/libs/**", "anon");

        chain.addPathDefinition("/favicon.ico", "anon");
        return chain;
    }

    /**
     * 管理端日志记录服务
     * @return
     */
    @Bean
    public LoggerService loggerService() {
        return new AdminLoggerService();
    }

    /**
     * 用户上线 事件监听
     * @return
     */
    @Bean
    public FrameworkListener onlineListener(IOnlineUserService service) {
        return new OnlineListener((event)-> {

            OnlineType type = event.getType();

            OnlineUser onlineUser = new OnlineUser((String) event.getSource());


            if(type == OnlineType.Guest) { // 访客访问(访问但是未登录)
                UserAgent userAgent = event.getUserAgent();
                AppType appType = null;
                Browser browser = userAgent.getBrowser();
                if(browser != null) {
                    BrowserType browserType = browser.getBrowserType();
                    if(browserType == BrowserType.APP) {
                        appType = AppType.App;
                    } else if(browserType == BrowserType.MOBILE_BROWSER) {
                        appType = AppType.WebApp;
                    } else if(browserType == BrowserType.WEB_BROWSER) {
                        appType = AppType.Web;
                    } else {
                        appType = AppType.Unknown;
                    }
                }
                onlineUser.setBrowse(browser.getName())
                        .setAccessIp(event.getAccessIp()).setUserNick("访客")
                        .setCreateTime(event.getAccessTime()).setLocation("")
                        .setOs(userAgent.getOperatingSystem().getName())
                        .setAppType(appType).setExpireTime(event.getTimeout());

                service.insert(onlineUser);
            } else if(type == OnlineType.On) { // 用户上线
                Admin admin = (Admin) event.getAdmin(); // 当前登录的用户

                onlineUser.setLoginTime(new Date())
                        .setStatus(type.name())
                        .setAccount(admin.getAccount())
                        .setUserNick(admin.getName());

                service.updateBySessionId(onlineUser);
            } else if(type == OnlineType.Off) { // 用户下线
                onlineUser.setStatus(type.name());
                service.updateBySessionId(onlineUser);
            }
        });
    }

}
