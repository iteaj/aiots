package com.iteaj.iboot.plugin.oauth2.service;

import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.spi.auth.AuthType;
import com.iteaj.framework.spi.oauth2.OAuth2Service;
import com.iteaj.framework.spi.oauth2.Oauth2Result;
import com.iteaj.iboot.plugin.oauth2.config.Oauth2ClientProperties;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.stereotype.Service;

/**
 * gitee 授权服务: 基于 JustAuth 实现框架 {@link OAuth2Service} SPI
 * <p>只负责纯第三方授权(生成授权页URL、code 换用户信息), 不涉本地账号;
 * 本地登录/建号由 core 消费本服务返回的 {@link Oauth2Result} 完成。</p>
 *
 * @author iteaj
 */
@Service
public class GiteeOAuth2Service implements OAuth2Service {

    private final Oauth2ClientProperties properties;

    public GiteeOAuth2Service(Oauth2ClientProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean support(AuthType type) {
        return AuthType.Gitee == type;
    }

    @Override
    public String authorize(AuthType type, String state) {
        return giteeRequest().authorize(state);
    }

    @Override
    public Oauth2Result getUserInfo(AuthType type, String code, String state) {
        AuthResponse<AuthUser> response = giteeRequest().login(AuthCallback.builder().code(code).state(state).build());
        if (response == null || !response.ok()) {
            throw new IllegalStateException("gitee授权失败: " + (response == null ? "无响应" : response.getMsg()));
        }
        return new JustAuthOauth2Result(AuthType.Gitee, response.getData());
    }

    /** 根据配置构建 gitee 授权请求(每次新建, JustAuth 请求对象轻量) */
    private AuthRequest giteeRequest() {
        Oauth2ClientProperties.Platform gitee = properties.getGitee();
        if (StrUtil.isBlank(gitee.getClientId()) || StrUtil.isBlank(gitee.getClientSecret())) {
            throw new IllegalStateException("gitee授权配置缺失: 请配置 oauth2.client.gitee.client-id / oauth2.client.gitee.client-secret");
        }
        AuthConfig config = AuthConfig.builder()
                .clientId(gitee.getClientId())
                .clientSecret(gitee.getClientSecret())
                .redirectUri(properties.getRedirectUri())
                .build();
        return new AuthGiteeRequest(config);
    }
}
