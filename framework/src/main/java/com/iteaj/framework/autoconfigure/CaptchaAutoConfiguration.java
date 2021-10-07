package com.iteaj.framework.autoconfigure;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.iteaj.framework.captcha.KaptchaCreator;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 校验码功能
 * @author iteaj
 * @since 1.0
 */
@RequestMapping("/valid")
@Controller("CaptchaController")
public class CaptchaAutoConfiguration {

    @Autowired
    private Producer producer;

    /**
     * 获取验证码
     * @return
     */
    @GetMapping("/captcha")
    public void captcha(HttpSession session, HttpServletResponse response) throws IOException {
        try(ServletOutputStream out = response.getOutputStream()){
            handleResponseHeader(response);

            // 创建要生产的文本已经图片
            // MathTextCreator对象创建的文本用'@'分割文本和值 e.g. 3+5=?@8
            String[] split = producer.createText().split("@");
            BufferedImage image = producer.createImage(split[0]);

            /**
             * 放入Session
             */
            session.setAttribute(Constants.KAPTCHA_SESSION_KEY
                    , split.length==2 ? split[1] : split[0]);
            /**
             * 放入的时间
             */
            session.setAttribute(Constants.KAPTCHA_SESSION_DATE
                    , System.currentTimeMillis());

            // 写入图片到输出流
            ImageIO.write(image, "jpg", out);
            out.flush();
        }
    }

    private void handleResponseHeader(HttpServletResponse response) {
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
    }

    @Bean
    @Order(20)
    public OrderFilterChainDefinition captchaFilterChainDefinition() {
        OrderFilterChainDefinition chain = new OrderFilterChainDefinition();

        chain.addPathDefinition("/valid/captcha", "anon");
        return chain;
    }

    /**
     * 字符格式的验证码
     * @return
     */
    @Bean(name = "charCaptcha")
    @ConditionalOnProperty(value = "msn.core.captcha-type", havingValue = "char")
    public Producer charCaptcha(){
        return KaptchaCreator.charInstance();
    }

    /**
     * 数学计算加、减、乘格式的验证码
     * @return
     */
    @Primary
    @Bean(name = "mathCaptcha")
    @ConditionalOnProperty(value = "msn.core.captcha-type", havingValue = "math")
    public Producer mathCaptcha(){
        return KaptchaCreator.mathInstance();
    }
}
