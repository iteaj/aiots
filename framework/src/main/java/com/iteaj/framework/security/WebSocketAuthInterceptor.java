package com.iteaj.framework.security;

import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * WebSocket握手鉴权拦截器SPI(鉴权模块实现, 业务模块复用)
 * <p>浏览器WebSocket API无法自定义请求头, 客户端以query参数携带token连接
 * (参数名与系统HTTP统一, 见FrameworkProperties.web.session.tokenName, 如 ws://host/ws/page?access_token=xxx),
 * 实现方在{@link #beforeHandshake}校验凭据,
 * 通过后将登录用户标识写入会话属性(key见CoreConst.WEB_SOCKET_USER_ID),
 * 连接建立后业务处理器从session attributes读取该标识注册连接管理; 校验失败返回401拒绝握手</p>
 * <p>sa-token模块的SaTokenWebSocketAuthInterceptor为默认实现: 业务模块注册WebSocket端点时
 * 注入本接口(如PageWebSocketConfig)即自动获得握手鉴权, 无需各自实现</p>
 */
public interface WebSocketAuthInterceptor extends HandshakeInterceptor {
    
}
