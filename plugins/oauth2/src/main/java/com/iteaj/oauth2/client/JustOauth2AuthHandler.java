package com.iteaj.oauth2.client;

import com.iteaj.framework.FrameworkException;
import com.iteaj.framework.Proxy.ProxyUtils;
import com.iteaj.framework.spi.app.WebAuthAction;
import com.iteaj.framework.spi.auth.AuthContext;
import com.iteaj.framework.spi.auth.AuthException;
import com.iteaj.framework.spi.auth.AuthType;
import com.iteaj.framework.spi.oauth2.*;
import com.iteaj.framework.spring.boot.config.FrameworkProperties;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.CacheManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    private Map<AuthType, Class<? extends AuthRequest>> authRequestMap = new HashMap<>(16);

    public JustOauth2AuthHandler(List<WebAuthAction> actions, CacheManager cacheManager) {
        super(actions, cacheManager);
    }

    @Override
    protected void doHandle(AuthContext context, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AuthType type = context.getType();
        String sessionId = request.getSession().getId();

        String domain = getConfig().getDomain();
        if(domain != null) {
            context.put("domain", domain);
        }

        AuthRequest authRequest = getAuthRequest(context, type);
        if(authRequest == null) {
            throw new FrameworkException("未开启对应的授权类型["+type+
                    "], 请增加配置[izone.web.app.oauth2.client.]"+type+"..");
        }

        String authorize = authRequest.authorize(sessionId);

        /**
         * 缓存state和context的映射
         * 在Oauth2重定向授权之后需要通过state获取context
         * @see #login(HttpServletRequest, HttpServletResponse)
         */
        final WebAuthAction action = getAction(context.getAction());

        final Object target = ProxyUtils.getTarget(action);
        if(target instanceof OAuth2Action) {
            ((OAuth2Action) target).doAuthorize(authorize, request, response);
        } else {
            throw new AuthException("["+action.getName()+"]不是[OAuth2Action]的实现");
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
            final FrameworkProperties.Oauth2 oauth2 = getConfig().getOauth2();
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

    protected void doOauth2Login(AuthContext authContext, HttpServletRequest request, HttpServletResponse response) {
        String state = request.getParameter("state");
        String code = request.getParameter("code");
        String authCode = request.getParameter("auth_code");

        // 获取执行动作
        String action = authContext.getAction();
        WebAuthAction authAction = getAction(action);

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

            if(authAction != null) {
                OAuth2User oAuth2User = new OAuth2User();
                BeanUtils.copyProperties(authUser, oAuth2User);

                // 执行授权回调
                authAction.login(oAuth2User, request, response);
            }
        } else {
            throw new AuthException("["+authContext.getType()
                    +"]授权失败 code["+authResponse.getCode()
                    +"] - msg["+authResponse.getMsg()+"]");
        }
    }

    private AuthRequest instanceAuthRequest(AuthContext context) {
        AuthConfig authConfig = new AuthConfig();
        final String redirectUri = getConfig().getOauth2().getRedirectUri();

        try {
            BeanUtils.copyProperties(context.get("Oauth2Config"), authConfig);

            Class<? extends AuthRequest> aClass = authRequestMap.get(context.getType());
            Constructor<? extends AuthRequest> constructor = aClass.getConstructor(AuthConfig.class);

            authConfig.setRedirectUri(context.get("domain") + redirectUri);
            return BeanUtils.instantiateClass(constructor, authConfig);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (me.zhyd.oauth.exception.AuthException e) {
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
