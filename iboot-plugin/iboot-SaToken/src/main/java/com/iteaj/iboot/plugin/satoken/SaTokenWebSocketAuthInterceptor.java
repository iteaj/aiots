package com.iteaj.iboot.plugin.satoken;

import cn.dev33.satoken.stp.StpUtil;
import com.iteaj.framework.autoconfigure.FrameworkProperties;
import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.security.WebSocketAuthInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;

import java.util.Map;

/**
 * Sa-Token WebSocket握手鉴权拦截器(全局生效)
 * <p>浏览器WebSocket无法自定义请求头, 客户端以query参数携带token连接
 * (如 ws://host/ws/ai/agent?access_token=xxx): 握手阶段用Sa-Token校验token并解析登录用户,
 * 通过后将用户标识写入会话属性(CoreConst.WEB_SOCKET_USER_ID)供业务处理器
 * 在连接建立后注册连接, 校验失败返回401拒绝握手</p>
 * <p>token参数名与系统统一(HTTP的header/cookie同源): 取framework.web.session.token-name
 * 配置(默认access_token, 见{@link FrameworkProperties}), 前端据此携带token</p>
 * <p>由SaTokenAutoConfiguration按satoken profile注册为bean, 业务模块注册
 * WebSocket端点时注入{@link WebSocketAuthInterceptor}即自动复用</p>
 */
@Slf4j
public class SaTokenWebSocketAuthInterceptor implements WebSocketAuthInterceptor {

    private final FrameworkProperties frameworkProperties;

    public SaTokenWebSocketAuthInterceptor(FrameworkProperties frameworkProperties) {
        this.frameworkProperties = frameworkProperties;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response
            , WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String tokenName = frameworkProperties.getWeb().getSession().getTokenName();
        String token = null;
        if (request instanceof ServletServerHttpRequest) {
            token = ((ServletServerHttpRequest) request).getServletRequest().getParameter(tokenName);
        }
        Object loginId = StpUtil.getLoginIdByToken(token);
        if (loginId == null) {
            // 握手阶段用HTTP 401拒绝时浏览器WebSocket API拿不到状态码(onclose仅code=1006), 无法区分未授权与网络故障;
            // 改为放行握手但不写入登录用户标识, 由处理器在连接建立后以close码4401关闭(前端据此跳登录), 见PageWebSocketHandler
            log.warn("WebSocket握手鉴权失败: 无效token, 放行握手后由处理器回传4401关闭");
            return true;
        }
        attributes.put(CoreConst.WEB_SOCKET_USER_ID, loginId.toString());
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response
            , WebSocketHandler wsHandler, Exception exception) {
    }
}
