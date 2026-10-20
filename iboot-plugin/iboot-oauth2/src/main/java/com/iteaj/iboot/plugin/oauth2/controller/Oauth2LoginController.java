package com.iteaj.iboot.plugin.oauth2.controller;

import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.OpenToken;
import com.iteaj.framework.security.SecurityUtil;
import com.iteaj.framework.spi.auth.AuthType;
import com.iteaj.framework.spi.oauth2.OAuth2ClientService;
import com.iteaj.framework.spi.oauth2.OAuth2ClientUser;
import com.iteaj.framework.spi.oauth2.OAuth2Service;
import com.iteaj.framework.spi.oauth2.Oauth2Result;
import com.iteaj.iboot.plugin.oauth2.service.Oauth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 第三方 OAuth2 授权登录入口(网页端 gitee 等)
 * <p>职责: 授权跳转与回调落地; 具体授权能力由框架 {@link OAuth2Service} SPI 提供(iboot-oauth2 插件基于 JustAuth 实现)。
 * 流程: /core/oauth2 生成授权页URL -> 前端跳转第三方 -> 第三方回调 /core/oauth2/callback
 * -> 换第三方用户 -> 查绑定/自动建号 -> StpUtil 登录 -> 302 回前端登录页带 token。</p>
 *
 * @author iteaj
 */
@RestController
@RequestMapping("/oauth2/client")
public class Oauth2LoginController extends BaseController {

    /** session 中缓存 state 与授权类型的 key(防 CSRF 与回调时定位平台) */
    private static final String STATE_KEY = "core_oauth2_state";
    private static final String TYPE_KEY = "core_oauth2_type";
    private static final String ORIGIN_KEY = "core_oauth2_origin";

    private final List<OAuth2Service> oauth2Services;
    private final OAuth2ClientService oAuth2ClientService;

    public Oauth2LoginController(List<OAuth2Service> oauth2Services, OAuth2ClientService oAuth2ClientService) {
        this.oauth2Services = oauth2Services;
        this.oAuth2ClientService = oAuth2ClientService;
    }

    /**
     * 授权入口: 返回第三方授权页URL, 前端 location.href 跳转
     * @param type 授权类型(gitee)
     */
    @GetMapping("authorize")
    public Result<String> authorize(String type, String url, HttpServletRequest request) {
        if(StrUtil.isBlank(url)) {
            return fail("url地址不能为空");
        }

        AuthType authType = parseType(type);
        OAuth2Service service = matchService(authType);
        String state = UUID.randomUUID().toString().replace("-", "");
        HttpSession session = request.getSession(true);
        session.setAttribute(ORIGIN_KEY, url);
        session.setAttribute(STATE_KEY, state);
        session.setAttribute(TYPE_KEY, authType.name());
        return success(service.authorize(authType, state), "授权地址");
    }

    /**
     * 授权回调: code 换第三方用户 -> 查绑定/自动建号 -> 登录 -> 重定向回前端带 token
     */
    @GetMapping("callback")
    public void callback(String code, String state, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            HttpSession session = request.getSession(false);
            Object cachedState = session == null ? null : session.getAttribute(STATE_KEY);
            if (cachedState == null || !cachedState.equals(state)) {
                response.sendRedirect("");
            }

            String typeName = (String) session.getAttribute(TYPE_KEY);
            String origin = (String) session.getAttribute(ORIGIN_KEY);
            session.removeAttribute(STATE_KEY);
            session.removeAttribute(TYPE_KEY);
            session.removeAttribute(ORIGIN_KEY);

            if (StrUtil.isBlank(code)) {
                redirect(origin + "/#/oauth2/login", response, "授权码为空");
                return;
            }

            AuthType authType = StrUtil.isBlank(typeName) ? AuthType.Gitee : AuthType.valueOf(typeName);
            Oauth2Result user = matchService(authType).getUserInfo(authType, code, state);
            session.setAttribute("oauth2_user", user);
            redirect(origin + "/#/oauth2/login", response, "登录成功");
        } catch (Exception e) {
            logger.error("Oauth2客户端授权登录失败", e);
        }
    }

    @PostMapping("login")
    public Result<String> login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if(session == null) {
            return fail("登录失败, 请重新发起登录");
        }

        Oauth2Result user = (Oauth2Result) session.getAttribute("oauth2_user");
        if(user == null) {
            return fail("登录失败, 请重新发起登录");
        }

        OAuth2ClientUser clientUser = oAuth2ClientService.getAndCreateOAuth2ClientUser(user);
        SecurityUtil.login(new OpenToken(user.getToken(), clientUser.getAccount()), request, response);
        return success("登录成功");
    }

    private void redirect(String url, HttpServletResponse response, String msg) throws IOException {
        response.sendRedirect(url);
    }

    private OAuth2Service matchService(AuthType type) {
        return oauth2Services.stream().filter(s -> s.support(type)).findFirst()
                .orElseThrow(() -> new IllegalStateException("未找到授权服务实现: " + type));
    }

    private AuthType parseType(String type) {
        if (StrUtil.isBlank(type)) {
            throw new ServiceException("授权类型不能为空");
        }
        for (AuthType t : AuthType.values()) {
            if (t.name().equalsIgnoreCase(type.trim())) {
                return t;
            }
        }
        throw new ServiceException("不支持的授权类型: " + type);
    }

}
