package com.iteaj.admin;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.google.code.kaptcha.Producer;
import com.iteaj.framework.AbstractWebApplication;
import com.iteaj.framework.api.AuthorizingService;
import com.iteaj.framework.captcha.KaptchaCreator;
import com.iteaj.framework.shiro.UserPasswordRealm;
import com.iteaj.framework.spring.boot.config.ShiroConfiguration;
import org.apache.shiro.realm.Realm;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * create time: 2020/2/28
 *
 * @author iteaj
 * @since 1.0
 */
@ImportAutoConfiguration(value = ShiroConfiguration.class)
@SpringBootApplication(scanBasePackages = "com.iteaj.**")
public class AdminWebApplication implements AbstractWebApplication {

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "${bind.type}"))
    AuthorizingService authorizingService;

    public static void main(String[] args) {
        SpringApplication.run(AdminWebApplication.class, args);
    }

    /**
     *  以用户名密码方式登录的权限Realm
     * @return
     */
    @Bean
    public Realm userPasswordRealm() {
        return new UserPasswordRealm(authorizingService);
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
