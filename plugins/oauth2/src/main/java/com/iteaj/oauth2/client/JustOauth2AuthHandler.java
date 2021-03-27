package com.iteaj.oauth2.client;

import com.iteaj.framework.FrameworkException;
import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.app.AppAuthAction;
import com.iteaj.framework.spi.app.AuthContext;
import com.iteaj.framework.spi.app.AuthException;
import com.iteaj.framework.spi.app.AuthType;
import com.iteaj.framework.spi.oauth2.AbstractOAuth2Handler;
import com.iteaj.framework.spi.oauth2.OAuth2Config;
import com.iteaj.framework.spi.oauth2.OAuth2Token;
import com.iteaj.framework.spi.oauth2.OAuth2User;
import com.iteaj.framework.spring.boot.config.AppConfig;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create time: 2021/3/25
 *  Oauth2客户端的授权处理器 比如：gitee, github, 微信公众号, 支付宝生活号等授权
 * @author iteaj
 * @since 1.0
 */
public class JustOauth2AuthHandler extends AbstractOAuth2Handler implements InitializingBean {

    private CacheManager cacheManager;
    private Map<AuthType, Class<? extends AuthRequest>> authRequestMap = new HashMap<>(16);

    public JustOauth2AuthHandler(List<AppAuthAction> actions, CacheManager cacheManager) {
        super(actions);
        this.cacheManager = cacheManager;
    }

    @Override
    protected void doOauth2Handle(AuthContext context, HttpServletRequest request, HttpServletResponse response) {
        AuthType type = context.getType();
        String sessionId = request.getSession().getId();

        String domain = getConfig().getApp().getDomain();
        if(domain != null) {
            context.put("domain", domain);
        }

        AuthRequest authRequest = getAuthRequest(context, type);
        if(authRequest == null) {
            throw new FrameworkException("未开启对应的授权类型["+type+
                    "], 请增加配置[izone.app.oauth2.client.]"+type+"..");
        }

        try {
            String authorize = authRequest.authorize(sessionId);

            /**
             * 缓存state和context的映射
             * 在Oauth2重定向授权之后需要通过state获取context
             * @see #login(HttpServletRequest, HttpServletResponse)
             */
            this.cacheStateAndContextMap(sessionId, context);

            response.sendRedirect(authorize);
        } catch (Exception e) {
            logger.error("oauth2发送重定向失败", e);
        }
    }

    private AuthRequest getAuthRequest(AuthContext context, AuthType type) {

        // 获取默认的配置信息
        OAuth2Config auth2Config = null;

        // 获取用户自定义的配置信息
        Serializable oauth2Config = context.get("Oauth2Config");
        if(oauth2Config instanceof OAuth2Config) { // 使用自定义配置
            auth2Config = (OAuth2Config) oauth2Config;
        } else {
            AppConfig.Oauth2 oauth2 = getConfig().getApp().getOauth2();
            if(oauth2 != null && oauth2.getClient() != null) {
                auth2Config = oauth2.getClient().get(type);

                // 保存当前授权配置
                context.put("Oauth2Config", auth2Config);
            }
        }

        // 找不到授权类型对应的配置
        if(auth2Config == null) {
            logger.error("找不到[{}]授权对应的配置[null] 两种配置如下：\r\n  1. 配置文件指定 [izone.app.oauth2.client.{}.xxx] " +
                    "\r\n  2. [AuthContext.put(\"Oauth2Config\", OAuth2Config)]", type, type);
            throw new AuthException("找不到["+type+"]授权对应的配置[null]");
        }

        // 使用用户自定义的域
        Serializable scopes = context.get("scopes");
        if(scopes instanceof List) {
            auth2Config.setScopes((List<String>) scopes);
        }

        return this.instanceAuthRequest(context);
    }

    protected void cacheStateAndContextMap(String sessionId, AuthContext context) {
        Cache cache = this.cacheManager.getCache(CoreConst.OAUTH2_STATE_CACHE);
        cache.put(sessionId, context);
    }

    @Override
    public void login(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        Object context = session.getAttribute(CoreConst.APP_SESSION_AUTH_CONTEXT);

        // 用户名密码登录
        if(context instanceof AuthContext &&
                ((AuthContext) context).getType() == AuthType.Account) {

            super.login(request, response);
        } else { // Oauth2授权登录
            doOauth2Login(request, response);
        }
    }

    private void doOauth2Login(HttpServletRequest request, HttpServletResponse response) {
        String state = request.getParameter("state");
        String code = request.getParameter("code");
        String authCode = request.getParameter("auth_code");
        Cache cache = this.cacheManager.getCache(CoreConst.OAUTH2_STATE_CACHE);

        try {
            AuthContext authContext = cache.get(state, AuthContext.class);
            if(authContext != null) {
                // 实例化授权请求
                AuthRequest authRequest = this.instanceAuthRequest(authContext);

                // 登录
                AuthCallback authCallback = new AuthCallback();
                authCallback.setCode(code);
                authCallback.setState(state);
                authCallback.setAuth_code(authCode);
                AuthResponse<AuthUser> authResponse = authRequest.login(authCallback);

                // 获取用户信息成功
                if(authResponse.ok()) {
                    AuthUser authUser = authResponse.getData();

                    // 获取执行动作
                    String action = authContext.getAction();
                    AppAuthAction authAction = getAction(action);

                    if(authAction != null) {
                        OAuth2User oAuth2User = new OAuth2User();
                        BeanUtils.copyProperties(authUser, oAuth2User);

                        // 执行授权回调
                        authAction.login(oAuth2User, request, response);
                    }
                } else {
                    logger.error("[{}]授权失败code: {} - msg: {}",
                            authContext.getType(), authResponse.getCode(), authResponse.getMsg());
                }
            } else {
                logger.warn("授权失败可能是state过期[{}]", state);
            }
        } finally {
            cache.evictIfPresent(state);
        }
    }

    private AuthRequest instanceAuthRequest(AuthContext context) {
        AuthConfig authConfig = new AuthConfig();
        try {
            BeanUtils.copyProperties(context.get("Oauth2Config"), authConfig);

            Class<? extends AuthRequest> aClass = authRequestMap.get(context.getType());
            Constructor<? extends AuthRequest> constructor = aClass.getConstructor(AuthConfig.class);

            authConfig.setRedirectUri(context.get("domain") + getConfig().getApp().getRedirectUri());
            return BeanUtils.instantiateClass(constructor, authConfig);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (me.zhyd.oauth.exception.AuthException e) {
            String redirectUri = getConfig().getApp().getRedirectUri();
            throw new AuthException("["+context.getType()+"]授权配置校验失败["+
                    authConfig+"], redirectUri: ["+redirectUri+"]");
        }

        return null;
    }

    @Override
    protected OAuth2Token refresh(String clientId) {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (AuthType type : AuthType.values()) {
            authRequestMap.put(type, getAuthRequest(type));
        }
    }

    private Class<? extends AuthRequest> getAuthRequest(AuthType type) {
        switch (type) {
            case Qq: return AuthQqRequest.class;
            case Jd: return AuthJdRequest.class;
            case Csdn: return AuthCsdnRequest.class;
            case Gitee: return AuthGiteeRequest.class;
            case Baidu: return AuthBaiduRequest.class;
            case Eleme: return AuthElemeRequest.class;
            case Alipay: return AuthAlipayRequest.class;
            case Huawei: return AuthHuaweiRequest.class;
            case Aliyun: return AuthAliyunRequest.class;
            case Google: return AuthGoogleRequest.class;
            case Github: return AuthGithubRequest.class;
            case TaoBao: return AuthTaobaoRequest.class;
            case DouYin: return AuthDouyinRequest.class;
            case TouTiao: return AuthToutiaoRequest.class;
            case MeiTuan: return AuthMeituanRequest.class;
            case WechatMp: return AuthWeChatMpRequest.class;
            case WechatOpen: return AuthWeChatOpenRequest.class;
            case WechatEnterprise: return AuthWeChatEnterpriseQrcodeRequest.class;
            case WechatEnterpriseWeb: return AuthWeChatEnterpriseWebRequest.class;
        }

        return null;
    }
}
