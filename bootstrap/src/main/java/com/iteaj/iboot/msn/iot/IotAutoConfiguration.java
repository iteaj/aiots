package com.iteaj.iboot.msn.iot;

import com.iteaj.iboot.msn.iot.collect.CollectActionFactory;
import com.iteaj.iboot.msn.iot.collect.action.*;
import com.iteaj.iot.config.ConnectProperties;
import com.iteaj.iot.modbus.server.dtu.ModbusRtuForDtuServerComponent;
import com.iteaj.iot.modbus.server.dtu.ModbusTcpForDtuServerComponent;
import com.iteaj.iot.plc.omron.OmronComponent;
import com.iteaj.iot.plc.siemens.SiemensS7Component;
import com.iteaj.iot.server.dtu.impl.CommonDtuServerComponent;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;

@Profile("iot")
@ComponentScan({"com.iteaj.iboot.msn.iot"})
@MapperScan({"com.iteaj.iboot.msn.iot.mapper"})
public class IotAutoConfiguration {

    /**
     * 启用modbus rtu 组件
     * @return
     */
    @Bean
    public ModbusRtuForDtuServerComponent modbusRtuForDtuServerComponent() {
        return new ModbusRtuForDtuServerComponent(new ConnectProperties(7058, 90, 0, 0));
    }

    /**
     * 启用modbus tcp 组件
     * @return
     */
    @Bean
    public ModbusTcpForDtuServerComponent modbusTcpForDtuServerComponent() {
        return new ModbusTcpForDtuServerComponent(new ConnectProperties(7068, 90, 0, 0));
    }

    /**
     * 通用dtu设备组件
     * @return
     */
    @Bean
    public CommonDtuServerComponent commonDtuServerComponent() {
        return new CommonDtuServerComponent(new ConnectProperties(7078, 90, 0, 0));
    }

    /**
     * 西门子S7 PLC组件
     * @return
     */
    @Bean
    public SiemensS7Component siemensS7Component() {
        return new SiemensS7Component();
    }

    /**
     * 欧姆龙PLC组件
     * @return
     */
    @Bean
    public OmronComponent omronComponent() {
        return new OmronComponent();
    }

    @Bean
    @ConditionalOnBean(OmronComponent.class)
    public OmronCollectAction omronCollectAction() {
        return new OmronCollectAction();
    }

    @Bean
    @ConditionalOnBean(SiemensS7Component.class)
    public SiemensCollectAction siemensCollectAction() {
        return new SiemensCollectAction();
    }

    @Bean
    @ConditionalOnBean(ModbusRtuForDtuServerComponent.class)
    public ModbusRtuForDTUCollectAction modbusRtuForDTUCollectAction() {
        return new ModbusRtuForDTUCollectAction();
    }

    @Bean
    @ConditionalOnBean(ModbusTcpForDtuServerComponent.class)
    public ModbusTcpForDTUCollectAction modbusTcpForDTUCollectAction() {
        return new ModbusTcpForDTUCollectAction();
    }

    @Bean
    @ConditionalOnBean(CommonDtuServerComponent.class)
    public CommonDtuCollectAction commonDtuCollectAction() {
        return new CommonDtuCollectAction();
    }

    @Bean
    public CollectActionFactory collectActionFactory() {
        return CollectActionFactory.getInstance();
    }
}
