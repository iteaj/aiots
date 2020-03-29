package com.iteaj.framework.web;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.api.AuthorizingService;
import com.iteaj.framework.authority.AuthException;
import com.iteaj.framework.authority.AuthService;
import com.iteaj.framework.spring.boot.config.FrameworkProperties;
import com.iteaj.framework.utils.ShiroUtils;
import com.iteaj.izone.spi.DefaultAuthToken;
import com.iteaj.izone.web.shiro.CommonAuthenticationException;
import com.iteaj.izone.web.shiro.ShiroTokenDecorate;
import com.iteaj.util.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

/**
 * create time: 2019/12/6
 *  认证服务
 * @author iteaj
 * @since 1.0
 */
@Controller
@RequestMapping
@ConditionalOnBean(AuthService.class)
public class FrameworkAuthController extends BaseController {

    private final Producer producer;
    private final AuthService authService;
    private final FrameworkProperties properties;
    @SofaReference(binding = @SofaReferenceBinding(bindingType = "${bind.type}"))
    private AuthorizingService authorizingService;

    public FrameworkAuthController(FrameworkProperties properties, Producer producer, AuthService authService) {
        this.producer = producer;
        this.properties = properties;
        this.authService = authService;
    }

    /**
     * 返回首页页面
     * @param request
     * @param response
     * @return
     */
    @GetMapping
    public String index(HttpServletRequest request, HttpServletResponse response) {
        return this.authService.index(request, response);
    }

    /**
     * 返回登录页面
     * @return
     */
    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        return this.authService.login(request, response);
    }

    /**
     * 表单提交登录功能
     * @param token
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    public Result login(@RequestBody DefaultAuthToken token) {
        try {
            if(StringUtils.isEmpty(token.getUserName())
                    || StringUtils.isEmpty(token.getPassword()))
                throw new AuthException("账号或密码错误");

            // 登录
            SecurityUtils.getSubject().login(new ShiroTokenDecorate(token));
        } catch (UnknownAccountException e) {
            logger.error("账号不存在", e);
            return fail("账号不存在");
        } catch (CredentialsException e) {
            logger.error("账号密码不匹配", e);
            return fail("账号密码不匹配");
        } catch (CommonAuthenticationException e) {
            logger.error(e.getMessage(), e);
            return fail(e.getMessage());
        } catch (AuthenticationException e) {
            logger.error("认证失败", e);
            return fail("认证失败");
        }

        return success().add("successUrl", properties.getAuth().getSuccessUrl());
    }

    /**
     * 注销
     * @param request
     * @param response
     */
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        return this.authService.logout(request, response);
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
             * 放入Session 之后会在此过滤器验证：{@link com.iteaj.framework.shiro.CaptchaFilter}
             */
            session.setAttribute(Constants.KAPTCHA_SESSION_KEY
                    , split.length==2 ? split[1] : split[0]);

            // 写入图片到输出流
            ImageIO.write(image, "jpg", out);
            out.flush();
        }
    }

    /**
     * 返回环境相关配置
     * @return
     */
    @ResponseBody
    @GetMapping("/env")
    public Result envConfig() {
        Map<String, Object> config =  authorizingService
                .getEnvConfig(ShiroUtils.getPrincipal());
        return success().add("env", config);
    }

    /**
     * 权限资源列表
     * @return
     */
    @ResponseBody
    @GetMapping("/resources")
    public Result resources() {
        BaseEntity principal = ShiroUtils.getPrincipal();
        return success().add("resources", authorizingService.getResources(principal));
    }

    /**
     * 获取视图菜单下面的功能点
     * @param id 指定视图
     * @return
     */
    @ResponseBody
    @GetMapping("/point")
    public Result getFunctionPoint(Long id) {
        return success().add("rows", authorizingService.getFunctionPoint(id));
    }

    private void handleResponseHeader(HttpServletResponse response) {
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
    }
}
