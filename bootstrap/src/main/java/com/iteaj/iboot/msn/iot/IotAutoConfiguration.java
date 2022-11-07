package com.iteaj.iboot.msn.iot;

import com.iteaj.iboot.msn.iot.collect.CollectActionFactory;
import com.iteaj.iboot.msn.iot.collect.action.*;
import com.iteaj.iboot.msn.iot.collect.store.MqttStoreAction;
import com.iteaj.iboot.msn.iot.collect.store.RDBMStoreAction;
import com.iteaj.iboot.msn.iot.collect.store.StoreActionFactory;
import com.iteaj.iot.client.mqtt.gateway.MqttGatewayComponent;
import com.iteaj.iot.client.mqtt.impl.DefaultMqttComponent;
import com.iteaj.iot.config.ConnectProperties;
import com.iteaj.iot.modbus.server.dtu.ModbusRtuForDtuServerComponent;
import com.iteaj.iot.modbus.server.dtu.ModbusTcpForDtuServerComponent;
import com.iteaj.iot.plc.omron.OmronComponent;
import com.iteaj.iot.plc.siemens.SiemensS7Component;
import com.iteaj.iot.server.dtu.impl.CommonDtuServerComponent;
import com.iteaj.iot.tools.db.rdbms.RdbmsSqlManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

@Profile("iot")
@ComponentScan({"com.iteaj.iboot.msn.iot"})
@MapperScan({"com.iteaj.iboot.msn.iot.mapper"})
@AutoConfigureAfter(JdbcTemplateAutoConfiguration.class)
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

    @Bean
    @ConditionalOnBean(JdbcTemplate.class)
    public RdbmsSqlManager rdbmsSqlManager(JdbcTemplate jdbcTemplate) {
        return new RdbmsSqlManager(jdbcTemplate);
    }

    /**
     * 关系型数据库采集存储
     * @return
     */
    @Bean
    @ConditionalOnBean(RdbmsSqlManager.class)
    public RDBMStoreAction rdbmStoreAction() {
        return new RDBMStoreAction();
    }

    /**
     * mqtt存储
     * @return
     */
    @Bean
    public MqttGatewayComponent mqttGatewayComponent() {
        return new MqttGatewayComponent();
    }

    @Bean
    public MqttStoreAction mqttStoreAction() {
        return new MqttStoreAction();
    }

    @Bean
    public StoreActionFactory storeActionFactory() {
        return StoreActionFactory.getInstance();
    }

    @Bean
    public DefaultMqttComponent defaultMqttComponent() {
        return new DefaultMqttComponent();
    }
}
