package com.iteaj.iboot.plugin.protocol.dtu;

import com.iteaj.framework.IVOption;
import com.iteaj.framework.ParamMeta;

public interface ModbusProtocolConsts {

    String MODBUS_POSITION = "寄存器地址(0-65535的整数)";

    ParamMeta[] networkConfig = new ParamMeta[]{
            ParamMeta.buildRequired("host", "主机", "绑定的网卡ip", "0.0.0.0"),
            ParamMeta.buildRequiredNumber("port", "端口", "5020", "监听的端口(1024-65535)"),
            ParamMeta.buildRequiredNumber("readIdle", "读空闲时长(秒)", 0, "多长时间没读到数据关闭链接"),
            ParamMeta.buildRequiredSelect("messageType", "注册码类型", "ASCII", "可选值(ASCII、HEX)"
                    , new IVOption("ASCII", "ASCII"), new IVOption("HEX", "HEX")),
    };

}
