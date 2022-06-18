package com.iteaj.iboot.msn.iot.websocket;

import com.iteaj.iot.CoreConst;
import com.iteaj.iot.Message;
import com.iteaj.iot.server.websocket.WebSocketServerListener;
import com.iteaj.iot.server.websocket.impl.DefaultWebSocketServerComponent;
import com.iteaj.iot.server.websocket.impl.DefaultWebSocketServerProtocol;
import com.iteaj.iot.websocket.HttpRequestWrapper;
import com.iteaj.iot.websocket.WebSocketFilter;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.Attribute;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;

import java.util.Arrays;

public class IotDebugListener implements WebSocketServerListener, WebSocketFilter<DefaultWebSocketServerComponent> {

   private final SessionManager securityManager;

    public IotDebugListener(SessionManager securityManager) {
        this.securityManager = securityManager;
    }

    @Override
    public String uri() {
        return "/iot/debug";
    }

    @Override
    public void onText(DefaultWebSocketServerProtocol protocol) {
        String s = protocol.readText();
        protocol.response("accept");
    }

    @Override
    public void onClose(DefaultWebSocketServerProtocol protocol) {

    }

    @Override
    public void onBinary(DefaultWebSocketServerProtocol protocol) {

    }

    /**
     * websocket 鉴权
     * @param request
     * @return
     */
    @Override
    public HttpResponseStatus authentication(HttpRequest request) {
        String sessionId = this.getSessionId(request);
        if(sessionId != null) {
            try {
                securityManager.getSession(new DefaultSessionKey(sessionId));
                return HttpResponseStatus.OK;
            } catch (Exception e) {
                return HttpResponseStatus.UNAUTHORIZED;
            }
        }

        return HttpResponseStatus.UNAUTHORIZED;
    }

    /**
     * 注册设备编号
     * @param head
     * @param equipCode
     * @param channel
     * @param component
     * @return
     */
    @Override
    public Message.MessageHead register(Message.MessageHead head, String equipCode, Channel channel, DefaultWebSocketServerComponent component) {
        if(equipCode == null) {
            Attribute<HttpRequestWrapper> request = channel.attr(CoreConst.WEBSOCKET_REQ);
            String sessionId = this.getSessionId(request.get().getRequest());
            head.setEquipCode(sessionId); // 使用sessionId作为设备编号
        }
        return head;
    }

    protected String getSessionId(HttpRequest request) {
        String cookie = request.headers().get("Cookie");
        return Arrays.stream(cookie.split(";"))
                .map(item -> item.split("="))
                .filter(item -> "JSESSIONID".equalsIgnoreCase(item[0].trim()))
                .findFirst().orElse(new String[]{null, null})[1];
    }
}
