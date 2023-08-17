package com.iteaj.framework.autoconfigure;

import com.iteaj.framework.exception.FrameworkException;
import com.iteaj.framework.exception.IBootControllerAdvice;
import com.iteaj.framework.logger.LoggerInterceptor;
import com.iteaj.framework.security.SecurityService;
import com.iteaj.framework.security.SecurityUtil;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web项目配置
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
     * 安全工具
     * @param securityService
     * @return
     */
    @Bean
    public SecurityUtil securityUtil(SecurityService securityService) {
        return SecurityUtil.getInstance(securityService);
    }

    /**
     * 注册日志拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if(properties.getWeb().isLogger()) {
            registry.addInterceptor(new LoggerInterceptor()).addPathPatterns("/**");
        }
    }

    @Bean
    public IBootControllerAdvice bootControllerAdvice() {
        return new IBootControllerAdvice();
    }

    /**
     * 用户处理上传文件的访问路径问题
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        FrameworkProperties.Upload upload = properties.getWeb().getUpload();
        String uploadRootUri = upload.getPattern();
        String uploadRootDir = upload.getLocation();
        if(uploadRootUri != null && uploadRootDir != null) {
            if(!uploadRootUri.endsWith("**")) {
                throw new FrameworkException("配置项[framework.web.upload.pattern]必须以**结尾");
            }

            if(!uploadRootDir.endsWith("/")) {
                uploadRootDir = uploadRootDir + "/";
            }

            registry.addResourceHandler(uploadRootUri)
                    .addResourceLocations(uploadRootDir);
        }

        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

}

