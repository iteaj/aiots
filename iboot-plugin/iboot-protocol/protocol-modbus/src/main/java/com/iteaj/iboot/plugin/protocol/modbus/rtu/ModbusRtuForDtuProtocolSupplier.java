package com.iteaj.iboot.plugin.protocol.modbus.rtu;

import com.iteaj.framework.spi.iot.NetworkConfigImpl;
import com.iteaj.framework.spi.iot.StaticProtocolSupplier;
import com.iteaj.framework.spi.iot.consts.CtrlMode;
import com.iteaj.framework.spi.iot.consts.ProtocolImplMode;
import com.iteaj.framework.spi.iot.consts.TransportProtocol;
import com.iteaj.framework.spi.iot.protocol.DefaultProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.iboot.plugin.protocol.modbus.tcp.ModbusTcpForDtuProtocolModeApi;
import com.iteaj.iot.FrameworkComponent;
import com.iteaj.iot.config.ConnectProperties;
import com.iteaj.iot.modbus.consts.ModbusCode;
import com.iteaj.iot.modbus.server.dtu.ModbusRtuForDtuServerComponent;

public class ModbusRtuForDtuProtocolSupplier implements StaticProtocolSupplier<NetworkConfigImpl.TcpNetworkConfig> {

    private DefaultProtocolModel protocolModel = new DefaultProtocolModel("MODBUS:RTU:DTU", CtrlMode.POINT, TransportProtocol.TCP)
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Read01.name(), "读线圈", ProtocolApiType.func, "按位读取寄存器值"))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Read02.name(), "读离散量输入", ProtocolApiType.func, "按位读取只读寄存器值"))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Read03.name(), "读保持寄存器", ProtocolApiType.func, "按字节读取寄存器值"))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Read04.name(), "读输入寄存器", ProtocolApiType.func, "按字节写入寄存器值"))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Write05.name(), "写单个线圈", ProtocolApiType.func, "按位写值到寄存器"))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Write06.name(), "写单个保持寄存器", ProtocolApiType.func, "按字节写值到寄存器"))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Write0F.name(), "写多个线圈", ProtocolApiType.func, "一次写多个位寄存器"))
            .addModelApi(new ModbusRtuForDtuProtocolModelApi(ModbusCode.Write10.name(), "写多个保持寄存器", ProtocolApiType.func, "一次写多个字节寄存器"));

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
