package com.iteaj.iboot.msn.iot;

import com.iteaj.iboot.msn.iot.debug.DebugHandle;
import com.iteaj.iboot.msn.iot.debug.DebugHandleFactory;
import com.iteaj.iboot.msn.iot.debug.websocket.WebsocketDebugListener;
import com.iteaj.iboot.msn.iot.server.debug.DebugServerComponent;
import com.iteaj.iot.config.ConnectProperties;
import org.apache.shiro.session.mgt.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 调试使用的配置类
 */
@Configuration
public class IotDebugConfiguration {

//    @Bean
    public DebugServerComponent debugServerComponent() {
        return new DebugServerComponent(new ConnectProperties(6158));
    }

    /**
     * 调试用的处理器工厂
     * @param handles
     * @return
     */
    @Bean
    public DebugHandleFactory debugHandleFactory(@Autowired(required = false) List<DebugHandle> handles) {
        return new DebugHandleFactory(handles);
    }

    /**
     * 调试需要用的websocket组件
     * @param sessionManager
     * @param handleFactory
     * @return
     */
    @Bean
    public WebsocketDebugListener websocketDebugListener(SessionManager sessionManager, DebugHandleFactory handleFactory) {
        return new WebsocketDebugListener(sessionManager, handleFactory);
    }
}
