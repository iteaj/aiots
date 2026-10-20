package com.iteaj.iboot.module.core.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 并发发送安全handler装饰器(透明包装, 业务handler零改动)
 * <p>同一WebSocket连接可能被并发发送(如工具指令推送sendToUser与心跳回执pong同时进行),
 * Tomcat底层状态机不允许一个partial写未完成时再次发送(报TEXT_PARTIAL_WRITING),
 * 本装饰器在连接建立时将回调session替换为{@link ConcurrentWebSocketSessionDecorator},
 * 同一连接的并发发送自动缓冲排队串行化(缓冲上限512KB/10秒, 超出则关闭连接)</p>
 * @author aiots-dev
 */
public class ConcurrentSessionWebSocketHandler extends WebSocketHandlerDecorator {

    /** 缓冲等待上限(毫秒), 超出视为连接不可用并关闭 */
    private static final int SEND_TIME_LIMIT = 10_000;

    /** 缓冲消息总大小上限(字节), 超出视为连接不可用并关闭 */
    private static final int BUFFER_SIZE_LIMIT = 512 * 1024;

    /** 原始session -> 并发安全包装session(连接级映射, 断线时移除) */
    private final ConcurrentHashMap<WebSocketSession, WebSocketSession> decoratedSessions = new ConcurrentHashMap<>();

    public ConcurrentSessionWebSocketHandler(WebSocketHandler delegate) {
        super(delegate);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        WebSocketSession decorated = new ConcurrentWebSocketSessionDecorator(session, SEND_TIME_LIMIT, BUFFER_SIZE_LIMIT);
        this.decoratedSessions.put(session, decorated);
        super.afterConnectionEstablished(decorated); // 业务handler注册/使用包装后的session
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(this.decoratedSessions.getOrDefault(session, session), message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(this.decoratedSessions.getOrDefault(session, session), exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        WebSocketSession decorated = this.decoratedSessions.remove(session);
        super.afterConnectionClosed(decorated == null ? session : decorated, closeStatus);
    }
}
