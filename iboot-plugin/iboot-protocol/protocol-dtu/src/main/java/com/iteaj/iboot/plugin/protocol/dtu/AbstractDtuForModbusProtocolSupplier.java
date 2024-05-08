package com.iteaj.iboot.plugin.protocol.dtu;

import com.iteaj.framework.spi.iot.NetworkConfigImpl;
import com.iteaj.framework.spi.iot.StaticServerProtocolSupplier;
import com.iteaj.framework.spi.iot.consts.*;
import com.iteaj.framework.spi.iot.protocol.*;
import com.iteaj.iboot.plugin.protocol.dtu.rtu.ModbusRtuForDtuProtocolModelApi;
import com.iteaj.iot.modbus.consts.ModbusCode;
import com.iteaj.iot.server.dtu.DtuDecoderServerComponent;

public abstract class AbstractDtuForModbusProtocolSupplier<C extends DtuDecoderServerComponent> extends StaticServerProtocolSupplier<NetworkConfigImpl.TcpNetworkConfig, C> {


    private DefaultProtocolModel protocolModel;

    public AbstractDtuForModbusProtocolSupplier(String code) {
        protocolModel = new DefaultProtocolModel(code, CtrlMode.POINT, TransportProtocol.TCP, "用于提供边缘网关设备Dtu+Modbus Rtu协议的支持")
                .addModelAttr(new ProtocolModelAttr(PointProtocolConfig.POINT_CHILD_SN, "从机地址", DataType.t_string, AttrType.IGNORE,"从机地址(1-255的值)"))
                .addModelAttr(new ProtocolModelAttr(PointProtocolConfig.POINT_ADDRESS, "寄存器地址", DataType.t_string, AttrType.IGNORE, "寄存器地址"))
                .addModelAttr(new ProtocolModelAttr(PointProtocolConfig.POINT_VALUE, "点位值", DataType.t_any, AttrType.IGNORE,"读/写寄存器的值(跟随数据类型转换)"))
                .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Read01.name(), "读线圈", ProtocolApiType.event, "按位读取寄存器值")
                        .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_CHILD_SN, ModbusProtocolConsts.MODBUS_CHILD_SN))
                        .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, ModbusProtocolConsts.MODBUS_POSITION))
                        .addUpConfig(ProtocolModelApiConfig.upBuild(PointProtocolConfig.POINT_VALUE, "读取到的值")))
                .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Read02.name(), "读离散量输入", ProtocolApiType.event, "按位读取只读寄存器值")
                        .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_CHILD_SN, ModbusProtocolConsts.MODBUS_CHILD_SN))
                        .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, ModbusProtocolConsts.MODBUS_POSITION))
                        .addUpConfig(ProtocolModelApiConfig.upBuild(PointProtocolConfig.POINT_VALUE, "读取到的值")))
                .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Read03.name(), "读保持寄存器", ProtocolApiType.event, "按字节读取寄存器值")
                        .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_CHILD_SN, ModbusProtocolConsts.MODBUS_CHILD_SN))
                        .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, ModbusProtocolConsts.MODBUS_POSITION))
                        .addUpConfig(ProtocolModelApiConfig.upBuild(PointProtocolConfig.POINT_VALUE, "读取到的值")))
                .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Read04.name(), "读输入寄存器", ProtocolApiType.event, "按字节写入寄存器值")
                        .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_CHILD_SN, ModbusProtocolConsts.MODBUS_CHILD_SN))
                        .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, ModbusProtocolConsts.MODBUS_POSITION))
                        .addUpConfig(ProtocolModelApiConfig.upBuild(PointProtocolConfig.POINT_VALUE, "读取到的值")))
                .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Write05.name(), "写单个线圈", ProtocolApiType.func, "按位写值到寄存器")
                        .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_CHILD_SN, ModbusProtocolConsts.MODBUS_CHILD_SN))
                        .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, ModbusProtocolConsts.MODBUS_POSITION))
                        .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_VALUE, "要写的值")))
                .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Write06.name(), "写单个保持寄存器", ProtocolApiType.func, "按字节写值到寄存器")
                        .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_CHILD_SN, ModbusProtocolConsts.MODBUS_CHILD_SN))
                        .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, ModbusProtocolConsts.MODBUS_POSITION))
                        .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_VALUE, "要写的值")))
                .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Write0F.name(), "写多个线圈", ProtocolApiType.func, "一次写多个位寄存器")
                        .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_CHILD_SN, ModbusProtocolConsts.MODBUS_CHILD_SN))
                        .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, ModbusProtocolConsts.MODBUS_POSITION))
                        .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_VALUE, "要写的值")))
                .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Write10.name(), "写多个保持寄存器", ProtocolApiType.func, "一次写多个字节寄存器")
                        .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_CHILD_SN, ModbusProtocolConsts.MODBUS_CHILD_SN))
                        .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, ModbusProtocolConsts.MODBUS_POSITION))
                        .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_VALUE, "要写的值")));
    }

    @Override
    public ProtocolModel getProtocol() {
        return this.protocolModel;
    }

    @Override
    public GatewayType getGatewayType() {
        return GatewayType.Gateway;
    }

    @Override
    public ProtocolImplMode getImplMode() {
        return ProtocolImplMode.Internal;
    }
}
