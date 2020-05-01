package com.iteaj.statics;

import com.google.code.kaptcha.Producer;
import com.iteaj.framework.captcha.KaptchaCreator;
import com.iteaj.framework.spring.boot.config.FrameworkProperties;
import com.iteaj.izone.web.shiro.OrderShiroFilterChainDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * create time: 2020/4/27
 *
 * @author iteaj
 * @since 1.0
 */
@Configuration
public class StaticAutoConfiguration implements WebMvcConfigurer {

    private final FrameworkProperties properties;

    public StaticAutoConfiguration(FrameworkProperties properties) {
        this.properties = properties;
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

    /**
     * Shiro权限认证的主路径拦截声明
     * @return
     */
    @Bean
    @Order(value = 8) //静态资源的访问拦截顺序必须靠前
    public OrderShiroFilterChainDefinition staticFilterChainDefinition() {
        OrderShiroFilterChainDefinition chain = new OrderShiroFilterChainDefinition();

        // 静态资源这里配置所有请求路径都可以匿名访问
        chain.addPathDefinition("/js/**", "anon");
        chain.addPathDefinition("/css/**", "anon");
        chain.addPathDefinition("/img/**", "anon");
        chain.addPathDefinition("/libs/**", "anon");

        chain.addPathDefinition("/login", "anon");
        chain.addPathDefinition("/captcha", "anon");
        return chain;
    }

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
}
