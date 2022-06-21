package com.iteaj.iboot.msn.iot;

import com.iteaj.iboot.msn.iot.debug.DebugHandle;
import com.iteaj.iboot.msn.iot.debug.DebugHandleFactory;
import com.iteaj.iboot.msn.iot.debug.websocket.WebsocketDebugListener;
import com.iteaj.iot.config.ConnectProperties;
import com.iteaj.iot.modbus.server.dtu.ModbusRtuForDtuServerComponent;
import com.iteaj.iot.modbus.server.dtu.ModbusTcpForDtuServerComponent;
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

    /**
     * 启用modbus rtu 组件
     * @return
     */
    @Bean
    public ModbusRtuForDtuServerComponent modbusRtuForDtuServerComponent() {
        return new ModbusRtuForDtuServerComponent(new ConnectProperties(7058));
    }

    /**
     * 启用modbus tcp 组件
     * @return
     */
    @Bean
    public ModbusTcpForDtuServerComponent modbusTcpForDtuServerComponent() {
        return new ModbusTcpForDtuServerComponent(new ConnectProperties(7068));
    }
}
