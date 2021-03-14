package com.iteaj.platform.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteaj.framework.security.AuthzUrl;
import com.iteaj.framework.spi.admin.event.OnlineListener;
import com.iteaj.framework.spi.admin.event.OnlineType;
import com.iteaj.framework.spi.event.Listener;
import com.iteaj.framework.spring.boot.config.FrameworkWebAutoConfiguration;
import com.iteaj.framework.spring.boot.config.FrameworkWebProperties;
import com.iteaj.modules.core.entity.Admin;
import com.iteaj.modules.core.entity.OnlineUser;
import com.iteaj.modules.core.enums.AppType;
import com.iteaj.modules.core.interceptor.DemoEnvInterceptor;
import com.iteaj.modules.core.service.IOnlineUserService;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Date;

//@EnableCaching
@ImportAutoConfiguration({
        FrameworkWebAutoConfiguration.class
})
@SpringBootApplication()
public class BootstrapBootApplication implements WebMvcConfigurer {


    private final FrameworkWebProperties properties;
    private final ObjectProvider<DemoEnvInterceptor> provider;

    public BootstrapBootApplication(FrameworkWebProperties properties, ObjectProvider<DemoEnvInterceptor> provider) {
        this.properties = properties;
        this.provider = provider;
    }


    public static void main(String[] args) {
        SpringApplication.run(BootstrapBootApplication.class, args);
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
     * @see com.iteaj.modules.core.controller.CommonUploadController 文件上传
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
