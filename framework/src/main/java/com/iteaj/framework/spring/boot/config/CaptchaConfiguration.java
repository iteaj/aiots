package com.iteaj.framework.spring.boot.config;

import com.google.code.kaptcha.Producer;
import com.iteaj.framework.captcha.KaptchaCreator;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

/**
 * create time: 2020/7/1
 *
 * @author iteaj
 * @since 1.0
 */
public class CaptchaConfiguration {

    @Bean
    @Order(20)
    public OrderFilterChainDefinition captchaFilterChainDefinition() {
        OrderFilterChainDefinition chain = new OrderFilterChainDefinition();

        chain.addPathDefinition("/captcha", "anon");
        return chain;
    }

    /**
     * 字符格式的验证码
     * @return
     */
    @Bean(name = "charCaptcha")
    @ConditionalOnProperty(value = "izone.web.admin.captcha-type", havingValue = "char")
    public Producer charCaptcha(){
        return KaptchaCreator.charInstance();
    }

    /**
     * 数学计算加、减、乘格式的验证码
     * @return
     */
    @Primary
    @Bean(name = "mathCaptcha")
    @ConditionalOnProperty(value = "izone.web.admin.captcha-type", havingValue = "math")
    public Producer mathCaptcha(){
        return KaptchaCreator.mathInstance();
    }
}
