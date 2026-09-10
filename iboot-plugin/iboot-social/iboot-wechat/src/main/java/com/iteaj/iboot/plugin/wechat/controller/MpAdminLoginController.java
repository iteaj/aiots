package com.iteaj.iboot.plugin.wechat.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.BaseController;
import com.iteaj.iboot.plugin.wechat.autoconfig.WechatProperties;
import com.iteaj.iboot.plugin.wechat.mp.MpService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 管理端微信公众号授权登录
 * @author iteaj
 */
@RestController
@RequestMapping("/wx/mp/admin")
@ConditionalOnProperty(prefix = "wechat.mp", name = "app-id")
public class MpAdminLoginController extends BaseController {

    private final MpService mpService;
    private final WechatProperties wechatProperties;

    public MpAdminLoginController(MpService mpService, WechatProperties wechatProperties) {
        this.mpService = mpService;
        this.wechatProperties = wechatProperties;
    }

    /**
     * 获取微信授权URL并重定向到微信授权页
     */
    @GetMapping("/auth")
    public void auth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String callbackUri = request.getRequestURL().toString().replace("/auth", "/callback");
        String url = mpService.buildAuthUrl(callbackUri, "admin_login");
        response.sendRedirect(url);
    }

    /**
     * 微信授权回调处理（管理端登录）
     * <p>已绑定系统账号的用户自动登录并重定向到管理端，未绑定用户提示错误。</p>
     */
    @GetMapping("/callback")
    public void callback(String code, String state, HttpServletResponse response) throws IOException {
        try {
            Long systemUserId = mpService.getSystemUserIdByWechat(code);

            // 通过SaToken创建登录会话
            StpUtil.login(systemUserId);
            String token = StpUtil.getTokenValue();

            // 重定向到管理端登录页，携带token参数
            String redirectUrl = getAdminLoginRedirectUrl(state, token);
            response.sendRedirect(redirectUrl);
        } catch (WxErrorException e) {
            redirectWithMsg(response, "微信授权失败：" + e.getMessage());
        } catch (Exception e) {
            redirectWithMsg(response, "登录失败：" + e.getMessage());
        }
    }

    /**
     * 构建管理端登录页重定向URL（携带token参数）
     */
    private String getAdminLoginRedirectUrl(String state, String token) {
        String baseUrl;
        if (StrUtil.isNotBlank(state) && !"admin_login".equals(state)) {
            baseUrl = state;
        } else {
            baseUrl = wechatProperties.getMp().getAdminLoginUrl();
        }

        if (StrUtil.isBlank(baseUrl)) {
            baseUrl = "/";
        }

        String separator = baseUrl.contains("?") ? "&" : "?";
        return baseUrl + separator + "token=" + token;
    }

    /**
     * 重定向到登录页并携带错误信息
     */
    private void redirectWithMsg(HttpServletResponse response, String msg) throws IOException {
        String baseUrl = wechatProperties.getMp().getAdminLoginUrl();
        if (StrUtil.isBlank(baseUrl)) {
            baseUrl = "/";
        }
        String separator = baseUrl.contains("?") ? "&" : "?";
        response.sendRedirect(baseUrl + separator + "wxError=" + URLEncoder.encode(msg, StandardCharsets.UTF_8));
    }
}
