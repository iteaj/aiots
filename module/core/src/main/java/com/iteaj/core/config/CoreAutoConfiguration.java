package com.iteaj.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteaj.core.app.OAuth2DemoAction;
import com.iteaj.core.auth.AdminAuthAction;
import com.iteaj.core.entity.Admin;
import com.iteaj.core.entity.OnlineUser;
import com.iteaj.core.enums.AppType;
import com.iteaj.core.service.*;
import com.iteaj.core.web.interceptor.DemoEnvInterceptor;
import com.iteaj.framework.security.AuthzUrl;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.framework.spi.admin.LoggerService;
import com.iteaj.framework.spi.admin.Module;
import com.iteaj.framework.spi.admin.ResourceManager;
import com.iteaj.framework.spi.admin.event.OnlineListener;
import com.iteaj.framework.spi.admin.event.OnlineType;
import com.iteaj.framework.spi.app.WebAuthAction;
import com.iteaj.framework.spi.event.AsyncGuavaEventBus;
import com.iteaj.framework.spi.event.EventPublisher;
import com.iteaj.framework.spi.event.Listener;
import com.iteaj.framework.spi.oauth2.OAuth2Action;
import com.iteaj.framework.spring.boot.config.FrameworkProperties;
import com.iteaj.framework.spring.condition.ConditionalOnFramework;
import com.iteaj.framework.spring.condition.Type;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Date;
import java.util.List;

/**
 * create time: 2020/4/25
 *
 * @author iteaj
 * @since 1.0
 */
@Configuration
public class CoreAutoConfiguration {

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
                return "核心模块";
            }
        };
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    /**
     * 事件通知发布者
     * @param taskExecutor
     * @param listeners
     * @return
     */
    @Bean
    public EventPublisher eventPublisher(TaskExecutor taskExecutor, List<Listener> listeners) {
        return new AsyncGuavaEventBus(listeners, taskExecutor);
    }

    @Bean
    public ResourceManager menuResourceManager(IMenuService menuService
            , IConfigService configService, List<Module> modules) {
        return new MenuResourceManager(menuService, configService, modules);
    }

    /**
     * 后台web管理端配置
     */
    @Configuration
    @ConditionalOnFramework(Type.Pc)
    public static class CoreWebAutoConfiguration implements WebMvcConfigurer {

        private final FrameworkProperties properties;
        private final ObjectProvider<DemoEnvInterceptor> provider;

        public CoreWebAutoConfiguration(FrameworkProperties properties
                , ObjectProvider<DemoEnvInterceptor> provider) {
            this.properties = properties;
            this.provider = provider;
        }

        /**
         * 后台管理端授权认证动作
         * @return
         */
        @Bean
        @Order(100)
        public WebAuthAction adminAuthAction() {
            final FrameworkProperties.Admin admin = properties.getAdmin();
            final String loginUrl = admin.getLoginUrl();
            return new AdminAuthAction("/core/**", loginUrl, "/");
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
         * @param logService
         * @param menuService
         * @return
         */
        @Bean
        public LoggerService loggerService(IOperaLogService logService, IMenuService menuService) {
            return new AdminLoggerService(logService, menuService);
        }

        @Bean
        public AdminAuthorizingService authorizingService() {
            return new AdminAuthorizingService();
        }

        /**
         * 主页解析
         * @param registry
         */
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/core/main").setViewName("main");
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            provider.ifAvailable(item->{
                registry.addInterceptor(item).addPathPatterns("/**");
            });
        }

        /**
         * 演示环境拦截器
         * @return
         */
        @Bean
        @ConditionalOnProperty(name = {"izb.env.demon"}, havingValue = "true")
        public DemoEnvInterceptor demoHandlerInterceptor(ObjectMapper objectMapper, AuthzUrl authzUrl) {
            return new DemoEnvInterceptor(objectMapper, authzUrl);
        }

        /**
         * 用户处理上传文件的访问路径问题
         * @see com.iteaj.core.web.controller.CommonUploadController 文件上传
         * @param registry
         */
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            String uploadRootUri = properties.getUploadRootUri();
            String uploadRootDir = properties.getUploadRootDir();
            if(uploadRootUri != null && uploadRootDir != null) {
                if(uploadRootUri.endsWith("/")) {
                    uploadRootUri = uploadRootUri+"**";
                } else {
                    uploadRootUri = uploadRootUri+"/**";
                }
                if(!uploadRootDir.endsWith("/")) {
                    uploadRootDir = uploadRootDir + "/";
                }

                registry.addResourceHandler(uploadRootUri)
                        .addResourceLocations("file:"+uploadRootDir);
            }
        }

        /**
         * 用户上线 事件监听
         * @return
         */
        @Bean
        public Listener onlineListener(IOnlineUserService service) {
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
}
