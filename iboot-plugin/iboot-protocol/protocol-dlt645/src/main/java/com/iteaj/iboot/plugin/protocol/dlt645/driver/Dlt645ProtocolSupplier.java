package com.iteaj.iboot.plugin.protocol.dlt645.driver;

import com.iteaj.framework.spi.iot.consts.*;
import com.iteaj.framework.spi.iot.protocol.DefaultProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelAttr;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645ProtocolType;

public interface Dlt645ProtocolSupplier {

    default ProtocolModel createProtocolModel(String protocolCode, TransportProtocol protocol) {
            return new DefaultProtocolModel(protocolCode, CtrlMode.POINT, protocol)
                .addModelAttr(new ProtocolModelAttr("value", "值", DataType.t_any, AttrType.IGNORE, "采集到的值"))
                .addModelAttr(new ProtocolModelAttr("data", "数据标识", DataType.t_string, AttrType.IGNORE, "长度为8的数据标识"))
                .addModelApi(new Dlt645ProtocolEventModelApi(Dlt645ProtocolType.readReq.name(), Dlt645ProtocolType.readReq.getDesc(), "读取电表数据")
                        .addDownConfig(ProtocolModelApiConfig.downBuild("data", "数据标识"))
                        .addUpConfig(ProtocolModelApiConfig.upBuild("value", "值")));
    }
}
