package com.iteaj.iboot.msn.iot;

import com.iteaj.iboot.msn.iot.websocket.IotDebugListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;

@Profile("iot")
@ComponentScan({"com.iteaj.iboot.msn.iot"})
@MapperScan({"com.iteaj.iboot.msn.iot.mapper"})
public class IotAutoConfiguration {

    @Bean
    public IotDebugListener iotDebugListener(SessionManager sessionManager) {
        return new IotDebugListener(sessionManager);
    }
}
