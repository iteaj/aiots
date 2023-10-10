package com.iteaj.iboot.plugin.protocol.modbus.rtu;

import com.iteaj.framework.spi.iot.NetworkConfigImpl;
import com.iteaj.framework.spi.iot.StaticProtocolSupplier;
import com.iteaj.framework.spi.iot.consts.CtrlMode;
import com.iteaj.framework.spi.iot.consts.ProtocolImplMode;
import com.iteaj.framework.spi.iot.consts.TransportProtocol;
import com.iteaj.framework.spi.iot.protocol.*;
import com.iteaj.iot.FrameworkComponent;
import com.iteaj.iot.config.ConnectProperties;
import com.iteaj.iot.modbus.consts.ModbusCode;
import com.iteaj.iot.modbus.server.dtu.ModbusRtuForDtuServerComponent;

public class ModbusRtuForDtuProtocolSupplier implements StaticProtocolSupplier<NetworkConfigImpl.TcpNetworkConfig> {

    private DefaultProtocolModel protocolModel = new DefaultProtocolModel("MODBUS:RTU:DTU", CtrlMode.POINT, TransportProtocol.TCP)
            .addModelAttr(new ProtocolModelAttr("childSn", "从机设备编号", "string", "从机设备编号"))
            .addModelAttr(new ProtocolModelAttr("position", "点位地址", "string", "寄存器地址"))
            .addModelAttr(new ProtocolModelAttr("value", "值", "object", "读/写寄存器里面的值(跟随数据类型转换)"))
            .addModelAttr(new ProtocolModelAttr("type", "数据类型", "string", "要读取或者写入的值的数据类型"))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Read01.name(), "读线圈", ProtocolApiType.func, "按位读取寄存器值")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("childSn", "从机设备编号"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("position", "点位地址"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("type", "数据类型"))
                    .addUpConfig(ProtocolModelApiConfig.upBuild("value", "读取到的值")))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Read02.name(), "读离散量输入", ProtocolApiType.func, "按位读取只读寄存器值")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("childSn", "从机设备编号"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("position", "点位地址"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("type", "数据类型"))
                    .addUpConfig(ProtocolModelApiConfig.upBuild("value", "读取到的值")))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Read03.name(), "读保持寄存器", ProtocolApiType.func, "按字节读取寄存器值")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("childSn", "从机设备编号"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("position", "点位地址"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("type", "数据类型"))
                    .addUpConfig(ProtocolModelApiConfig.upBuild("value", "读取到的值")))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Read04.name(), "读输入寄存器", ProtocolApiType.func, "按字节写入寄存器值")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("childSn", "从机设备编号"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("position", "点位地址"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("type", "数据类型"))
                    .addUpConfig(ProtocolModelApiConfig.upBuild("value", "读取到的值")))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Write05.name(), "写单个线圈", ProtocolApiType.func, "按位写值到寄存器")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("childSn", "从机设备编号"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("position", "点位地址"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("type", "数据类型"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("value", "要写的值")))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Write06.name(), "写单个保持寄存器", ProtocolApiType.func, "按字节写值到寄存器")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("childSn", "从机设备编号"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("position", "点位地址"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("type", "数据类型"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("value", "要写的值")))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Write0F.name(), "写多个线圈", ProtocolApiType.func, "一次写多个位寄存器")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("childSn", "从机设备编号"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("position", "点位地址"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("type", "数据类型"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("value", "要写的值")))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Write10.name(), "写多个保持寄存器", ProtocolApiType.func, "一次写多个字节寄存器")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("childSn", "从机设备编号"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("position", "点位地址"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("type", "数据类型"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("value", "要写的值")));

    @Override
    public String getDesc() {
        return "用于提供边缘网关设备Dtu+Modbus Rtu协议的支持";
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
    public FrameworkComponent createComponent(NetworkConfigImpl.TcpNetworkConfig config) {
        return new ModbusRtuForDtuServerComponent(new ConnectProperties(config.getHost(), config.getPort()));
    }
}
