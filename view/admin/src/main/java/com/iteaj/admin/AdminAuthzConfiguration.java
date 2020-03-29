package com.iteaj.admin;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.google.code.kaptcha.Producer;
import com.iteaj.framework.api.AuthorizingService;
import com.iteaj.framework.captcha.KaptchaCreator;
import com.iteaj.framework.shiro.UserPasswordRealm;
import com.iteaj.framework.spring.boot.config.ShiroConfiguration;
import org.apache.shiro.realm.Realm;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * create time: 2020/3/29
 *
 * @author iteaj
 * @since 1.0
 */
@Configuration
@MapperScan(basePackages = {"com.iteaj.admin.dao"})
@ImportAutoConfiguration(value = ShiroConfiguration.class)
public class AdminAuthzConfiguration {

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "${bind.type}"))
    private AuthorizingService authorizingService;

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
