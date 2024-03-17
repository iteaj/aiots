package com.iteaj.iboot.plugin.protocol.modbus.tcp;

import com.iteaj.framework.ParamMeta;
import com.iteaj.framework.spi.iot.NetworkConfigImpl;
import com.iteaj.framework.spi.iot.ServerProtocolSupplier;
import com.iteaj.framework.spi.iot.StaticProtocolSupplier;
import com.iteaj.framework.spi.iot.StaticServerProtocolSupplier;
import com.iteaj.framework.spi.iot.consts.CtrlMode;
import com.iteaj.framework.spi.iot.consts.ProtocolImplMode;
import com.iteaj.framework.spi.iot.consts.TransportProtocol;
import com.iteaj.framework.spi.iot.protocol.*;
import com.iteaj.iboot.plugin.protocol.modbus.ModbusProtocolConsts;
import com.iteaj.iboot.plugin.protocol.modbus.rtu.ModbusRtuForDtuProtocolModelApi;
import com.iteaj.iot.FrameworkComponent;
import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.config.ConnectProperties;
import com.iteaj.iot.modbus.consts.ModbusCode;
import com.iteaj.iot.modbus.server.dtu.ModbusTcpForDtuMessage;
import com.iteaj.iot.modbus.server.dtu.ModbusTcpForDtuServerComponent;

public class ModbusTcpForDtuProtocolSupplier extends StaticServerProtocolSupplier<NetworkConfigImpl.TcpNetworkConfig, ModbusTcpForDtuServerComponent> {

    private DefaultProtocolModel protocolModel = new DefaultProtocolModel("MODBUS:TCP:DTU", CtrlMode.POINT, TransportProtocol.TCP)
            .addModelAttr(new ProtocolModelAttr("childSn", "从机地址", "string", "从机地址"))
            .addModelAttr(new ProtocolModelAttr("position", "寄存器地址", "string", "寄存器地址"))
            .addModelAttr(new ProtocolModelAttr("value", "点位值", "object", "读/写寄存器的值(跟随数据类型转换)"))
            .addModelAttr(new ProtocolModelAttr("type", "数据类型", "string", "要读取或者写入的值的数据类型"))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Read01.name(), "读线圈", ProtocolApiType.event, "按位读取寄存器值")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("childSn", ModbusProtocolConsts.MODBUS_CHILD_SN))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("position", ModbusProtocolConsts.MODBUS_POSITION))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("type", ModbusProtocolConsts.MODBUS_TYPE))
                    .addUpConfig(ProtocolModelApiConfig.upBuild("value", "读取到的值")))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Read02.name(), "读离散量输入", ProtocolApiType.event, "按位读取只读寄存器值")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("childSn", ModbusProtocolConsts.MODBUS_CHILD_SN))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("position", ModbusProtocolConsts.MODBUS_POSITION))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("type", ModbusProtocolConsts.MODBUS_TYPE))
                    .addUpConfig(ProtocolModelApiConfig.upBuild("value", "读取到的值")))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Read03.name(), "读保持寄存器", ProtocolApiType.event, "按字节读取寄存器值")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("childSn", ModbusProtocolConsts.MODBUS_CHILD_SN))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("position", ModbusProtocolConsts.MODBUS_POSITION))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("type", ModbusProtocolConsts.MODBUS_TYPE))
                    .addUpConfig(ProtocolModelApiConfig.upBuild("value", "读取到的值")))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Read04.name(), "读输入寄存器", ProtocolApiType.event, "按字节写入寄存器值")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("childSn", ModbusProtocolConsts.MODBUS_CHILD_SN))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("position", ModbusProtocolConsts.MODBUS_POSITION))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("type", ModbusProtocolConsts.MODBUS_TYPE))
                    .addUpConfig(ProtocolModelApiConfig.upBuild("value", "读取到的值")))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Write05.name(), "写单个线圈", ProtocolApiType.func, "按位写值到寄存器")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("childSn", ModbusProtocolConsts.MODBUS_CHILD_SN))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("position", ModbusProtocolConsts.MODBUS_POSITION))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("type", ModbusProtocolConsts.MODBUS_TYPE))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("value", "要写的值")))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Write06.name(), "写单个保持寄存器", ProtocolApiType.func, "按字节写值到寄存器")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("childSn", ModbusProtocolConsts.MODBUS_CHILD_SN))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("position", ModbusProtocolConsts.MODBUS_POSITION))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("type", ModbusProtocolConsts.MODBUS_TYPE))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("value", "要写的值")))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Write0F.name(), "写多个线圈", ProtocolApiType.func, "一次写多个位寄存器")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("childSn", ModbusProtocolConsts.MODBUS_CHILD_SN))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("position", ModbusProtocolConsts.MODBUS_POSITION))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("type", ModbusProtocolConsts.MODBUS_TYPE))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("value", "要写的值")))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Write10.name(), "写多个保持寄存器", ProtocolApiType.func, "一次写多个字节寄存器")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("childSn", ModbusProtocolConsts.MODBUS_CHILD_SN))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("position", ModbusProtocolConsts.MODBUS_POSITION))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("type", ModbusProtocolConsts.MODBUS_TYPE))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("value", "要写的值")));

    @Override
    public String getDesc() {
        return "用于提供边缘网关设备Dtu+Modbus Tcp协议的支持";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public ProtocolModel getProtocol() {
        return protocolModel;
    }

    @Override
    public ProtocolImplMode getImplMode() {
        return ProtocolImplMode.Internal;
    }

    @Override
    public ParamMeta[] getProductConfig() {
        return new ParamMeta[] {
                ParamMeta.buildRequired("dataFormat", "数据格式", "可选[ABCD、BADC、CDAB、DCBA]", "ABCD")
        };
    }

    @Override
    public ParamMeta[] getGatewayConfig() {
        return new ParamMeta[]{
                ParamMeta.buildRequired("host", "主机", "", "0.0.0.0"),
                ParamMeta.buildRequired("port", "端口", null, "502")
        };
    }

    @Override
    public ModbusTcpForDtuServerComponent doCreateComponent(NetworkConfigImpl.TcpNetworkConfig config) {
        ConnectProperties connectProperties = new ConnectProperties(config.getHost(), config.getPort());
        if(config.getAllIdle() != null) {
            connectProperties.setAllIdleTime(config.getAllIdle());
        } else if(config.getReadIdle() != null) {
            connectProperties.setReaderIdleTime(config.getReadIdle());
        } else if(config.getWriteIdle() != null) {
            connectProperties.setWriterIdleTime(config.getWriteIdle());
        }

        return new ModbusTcpForDtuServerComponent(connectProperties);
    }
}
