package com.iteaj.iboot.plugin.social.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.plugin.social.autoconfig.WechatProperties;
import com.iteaj.iboot.plugin.social.entity.SocialUser;
import com.iteaj.iboot.plugin.social.wechat.mp.MpService;
import jakarta.servlet.http.HttpServletResponse;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 公众号接口
 * @author iteaj
 */
@RestController
@RequestMapping("/wx/mp")
@ConditionalOnProperty(prefix = "wechat.mp", name = "app-id")
public class MpController extends BaseController {

    private final MpService mpService;
    private final WechatProperties wechatProperties;

    public MpController(MpService mpService, WechatProperties wechatProperties) {
        this.mpService = mpService;
        this.wechatProperties = wechatProperties;
    }

    /**
     * 获取授权URL
     * @param redirectUri 回调地址
     * @param state state参数
     */
    @GetMapping("/auth")
    public Result<String> auth(String redirectUri, String state) {
        String url = mpService.buildAuthUrl(redirectUri, state);
        return success(url, "获取成功");
    }

    /**
     * 授权回调处理
     * <p>通过state区分绑定场景：
     * <ul>
     *   <li>state=bind_{userId} → 自动绑定到指定系统用户</li>
     *   <li>state为空 → 跳转到配置的绑定页，由移动端完成绑定</li>
     * </ul>
     */
    @GetMapping("/callback")
    public void callback(String code, String state, HttpServletResponse response) throws IOException {
        try {
            SocialUser socialUser = mpService.handleCallback(code);

            if (state != null && state.startsWith("bind_")) {
                // 从管理端发起的绑定（state包含用户ID）
                Long userId = Long.parseLong(state.substring(5));
                mpService.bindSystemUser(socialUser.getOpenId(), userId);

                String successUrl = wechatProperties.getMp().getSuccessPageUrl();
                if (successUrl != null && !successUrl.isEmpty()) {
                    response.sendRedirect(successUrl);
                } else {
                    response.getWriter().write("绑定成功");
                }
            } else {
                // 从公众号菜单进入，跳转到移动端提供的绑定页
                String bindPageUrl = wechatProperties.getMp().getBindPageUrl();
                if (bindPageUrl != null && !bindPageUrl.isEmpty()) {
                    String redirectUrl = bindPageUrl
                            + (bindPageUrl.contains("?") ? "&" : "?")
                            + "openId=" + URLEncoder.encode(socialUser.getOpenId(), StandardCharsets.UTF_8);
                    response.sendRedirect(redirectUrl);
                } else {
                    response.getWriter().write("请配置 wechat.mp.bind-page-url");
                }
            }
        } catch (WxErrorException e) {
            response.getWriter().write("授权失败：" + e.getMessage());
        }
    }

    /**
     * 微信服务器验证（GET方式，用于配置URL验证）
     */
    @GetMapping("/webhook")
    public String verify(String signature, String timestamp, String nonce, String echostr) {
        return mpService.handleVerify(timestamp, nonce, signature, echostr);
    }
}
