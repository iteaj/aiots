package com.iteaj.framework.autoconfigure;

import com.iteaj.framework.cache.CacheEntry;
import com.iteaj.framework.cache.SerializerType;
import com.iteaj.framework.captcha.CaptchaConfiguration;
import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.framework.spi.admin.SecurityUtils;
import com.iteaj.framework.spi.auth.FrameworkAuthServlet;
import com.iteaj.framework.spi.auth.WebAuthAction;
import com.iteaj.framework.spi.auth.WebAuthHandler;
import com.iteaj.framework.spi.auth.handle.AccountAuthHandler;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * web项目配置信息(包含浏览器[pc,app,微信,支付宝等], 小程序, app)
 */
@ComponentScan({
        "com.iteaj.*.web.**" // 扫描注入所有web模块
})
@ImportAutoConfiguration({
        CaptchaConfiguration.class,
        FrameworkAutoConfiguration.class,
        Oauth2Configuration.class
})
public class FrameworkWebConfiguration implements WebMvcConfigurer {

    private final FrameworkWebProperties properties;

    public FrameworkWebConfiguration(FrameworkWebProperties properties) {
        this.properties = properties;
    }

    /**
     * web session缓存
     * @return
     */
    @Bean
    public CacheEntry sessionCacheEntry() {
        final FrameworkWebProperties.Auth auth = properties.getAuth();
        return new CacheEntry(CoreConst.WEB_SESSION_CACHE, auth.getTimeout(), SerializerType.Jdk);
    }

    /**
     * app session缓存
     * @return
     */
    @Bean
    public CacheEntry appCacheEntry() {
        final FrameworkWebProperties.Auth auth = properties.getAuth();
        return new CacheEntry(CoreConst.APP_SESSION_CACHE, auth.getAppTimeout(), SerializerType.Jdk);
    }

    /**
     * 注销地址和oauth2重定向地址不能被过滤器拦截
     * @return
     */
    @Bean
    @Order(20)
    public OrderFilterChainDefinition oauth2FilterChainDefinition() {
        final OrderFilterChainDefinition definition = new OrderFilterChainDefinition();

        definition.addPathDefinition(properties.getAuth().getLogoutUri(), "anon");
        definition.addPathDefinition(properties.getOauth2().getRedirectUri(), "anon");
        return definition;
    }

    /**
     * 框架的授权,认证登录, 注销 Servlet
     * @param handler
     * @return
     */
    @Bean
    public ServletRegistrationBean frameworkAuthServlet(WebAuthHandler handler) {
        String redirectUri = properties.getOauth2().getRedirectUri();
        final String logoutUri = properties.getAuth().getLogoutUri();
        return new ServletRegistrationBean(new FrameworkAuthServlet(handler), redirectUri, logoutUri);
    }

    /**
     * 用户处理上传文件的访问路径问题
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
     * 使用账号密码认证的处理器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean({WebAuthHandler.class})
    public WebAuthHandler appAuthHandler(List<WebAuthAction> actions) {
        return new AccountAuthHandler(actions);
    }
}

