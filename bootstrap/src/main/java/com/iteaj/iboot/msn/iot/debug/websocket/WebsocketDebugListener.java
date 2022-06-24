package com.iteaj.iboot.msn.iot.debug.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.result.HttpResult;
import com.iteaj.iboot.msn.iot.debug.*;
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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

public class WebsocketDebugListener implements WebSocketServerListener, WebSocketFilter<DefaultWebSocketServerComponent> {

   private final SessionManager securityManager;
   private final DebugHandleFactory handleFactory;

    public WebsocketDebugListener(SessionManager securityManager, DebugHandleFactory handleFactory) {
        this.securityManager = securityManager;
        this.handleFactory = handleFactory;
    }

    @Override
    public String uri() {
        return "/ws/iot/debug";
    }

    @Override
    public void onText(DefaultWebSocketServerProtocol protocol) {
        HttpRequestWrapper request = protocol.requestMessage().request();
        Optional<String> type = request.getQueryParam("type");
        if(type.isPresent()) {
            Optional<DebugHandle> handle = this.handleFactory.getHandle(type.get());
            if(handle.isPresent()) {
                DebugHandle debugHandle = handle.get();
                DebugRequestModel parse = debugHandle.parse(protocol.readText());
                debugHandle.handle(parse.getModel(), new DebugWebsocketWrite(parse.getClientSn()));
            } else {
                protocol.response(JSON.toJSONString(HttpResult.Fail("未找到对应的处理器["+type.get()+"]")));
            }
        } else {
            protocol.response(JSON.toJSONString(HttpResult.Fail("未指定调试类型[type]")));
        }
    }

    @Override
    public void onClose(DefaultWebSocketServerProtocol protocol) {

    }

    @Override
    public void onBinary(DefaultWebSocketServerProtocol protocol) {

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
            request.get().getQueryParam("clientSn").ifPresent(clientSn -> head.setEquipCode(clientSn));
        }
        return head;
    }

    /**
     * 不做认证
     * @param request
     * @return
     */
    @Override
    public HttpResponseStatus authentication(HttpRequest request) {
        return HttpResponseStatus.OK;
    }
}
