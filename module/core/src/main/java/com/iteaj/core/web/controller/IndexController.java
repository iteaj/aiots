package com.iteaj.core.web.controller;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.iteaj.framework.BaseController;
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
 * create time: 2020/4/27
 *
 * @author iteaj
 * @since 1.0
 */

@Controller
@RequestMapping()
public class IndexController extends BaseController {

    private final Producer producer;

    public IndexController(Producer producer) {
        this.producer = producer;
    }

    /**
     * 登录页
     */
    @GetMapping("${izone.web.admin.login-url}")
    public String login() {
        return "login";
    }

    /**
     * 首页
     * @return
     */
    @GetMapping("${izone.web.admin.success-url}")
    public String index() {
        return "index";
    }

    @GetMapping("tindex")
    public String tindex() {
        return "tindex";
    }

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
}
