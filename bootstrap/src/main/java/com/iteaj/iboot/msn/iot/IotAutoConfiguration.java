package com.iteaj.iboot.msn.iot;

import com.iteaj.iboot.msn.iot.debug.DebugHandle;
import com.iteaj.iboot.msn.iot.debug.DebugHandleFactory;
import com.iteaj.iboot.msn.iot.debug.websocket.WebsocketDebugListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile("iot")
@ComponentScan({"com.iteaj.iboot.msn.iot"})
@MapperScan({"com.iteaj.iboot.msn.iot.mapper"})
public class IotAutoConfiguration {

    @Bean
    public DebugHandleFactory debugHandleFactory(@Autowired(required = false) List<DebugHandle> handles) {
        return new DebugHandleFactory(handles);
    }

    @Bean
    public WebsocketDebugListener websocketDebugListener(SessionManager sessionManager, DebugHandleFactory handleFactory) {
        return new WebsocketDebugListener(sessionManager, handleFactory);
    }
}
