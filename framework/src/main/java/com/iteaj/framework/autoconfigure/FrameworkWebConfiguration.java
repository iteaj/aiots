package com.iteaj.framework.autoconfigure;

import com.iteaj.framework.cache.CacheEntry;
import com.iteaj.framework.cache.SerializerType;
import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.auth.WebAuthAction;
import com.iteaj.framework.spi.auth.WebAuthHandler;
import com.iteaj.framework.spi.auth.handle.AccountAuthHandler;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * web项目配置信息(包含浏览器[pc,app,微信,支付宝等], 小程序, app)
 */
@ImportAutoConfiguration({
        FrameworkAutoConfiguration.class
})
public class FrameworkWebConfiguration implements WebMvcConfigurer {

    private final FrameworkProperties properties;

    public FrameworkWebConfiguration(FrameworkProperties properties) {
        this.properties = properties;
    }

    /**
     * web session缓存
     * @return
     */
    @Bean
    public CacheEntry sessionCacheEntry() {
        final FrameworkProperties.Auth auth = properties.getAuth();
        return new CacheEntry(CoreConst.WEB_SESSION_CACHE, auth.getTimeout(), SerializerType.Jdk);
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

    /**
     * 用户处理上传文件的访问路径问题
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadRootUri = properties.getUploadPattern();
        String uploadRootDir = properties.getUploadLocation();
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

        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

}

