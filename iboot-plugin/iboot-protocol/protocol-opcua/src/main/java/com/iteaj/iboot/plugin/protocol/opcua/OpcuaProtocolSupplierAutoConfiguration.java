package com.iteaj.iboot.plugin.protocol.opcua;

import com.iteaj.iboot.plugin.protocol.opcua.driver.OpcuaProtocolSupplier;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcDriver;
import org.apache.plc4x.java.api.messages.PlcDiscoveryItem;
import org.apache.plc4x.java.api.messages.PlcDiscoveryRequest;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.apache.plc4x.java.opcua.OpcuaPlcDriver;
import org.apache.plc4x.java.spi.connection.DefaultNettyPlcConnection;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

public class OpcuaProtocolSupplierAutoConfiguration implements InitializingBean {

    @Bean
    public OpcuaProtocolSupplier opcuaProtocolSupplier() {
        return new OpcuaProtocolSupplier();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 创建发现管理器
//        PlcDiscoveryManager discoveryManager =
        // 启动多播发现并获取发现结果列表
//        List<PlcDiscoveryItem> discoveryItems = discoveryManager.discover("opcua").get();
        PlcDriverManager driverManager = new PlcDriverManager();
        OpcuaPlcDriver opcua = (OpcuaPlcDriver)driverManager.getDriver("opcua");

        DefaultNettyPlcConnection plcConnection = (DefaultNettyPlcConnection) driverManager
                .getConnection("opcua:tcp://192.168.0.107:49320?discovery=false&username=iteaj&password=123456&userTokenPolicy=UserName");
        PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();
        PlcReadResponse response = builder.addItem("add", "ns=2;s=simulator.device1.device_name").build().execute().get();
        if(response.getResponseCode("add") == PlcResponseCode.OK) {
            Object add = response.getObject("add");
            System.out.println(add);
        }
        System.out.println(plcConnection);
    }
}
