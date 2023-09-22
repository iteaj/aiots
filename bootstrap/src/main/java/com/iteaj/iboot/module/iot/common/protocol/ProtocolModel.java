package com.iteaj.iboot.module.iot.common.protocol;

import com.iteaj.iboot.module.iot.consts.CtrlMode;
import com.iteaj.iboot.module.iot.consts.TransportProtocol;

import java.util.Map;

public interface ProtocolModel {

    /**
     * 协议代码
     * @return
     */
    String getCode();

    /**
     * 协议说明
     * @return
     */
    String getRemark();

    /**
     * 协议控制方式
     * @return
     */
    CtrlMode getCtrlMode();

    /**
     * 使用的传输协议
     * @return
     */
    TransportProtocol getType();

    /**
     * 协议模型接口
     * @see java.util.LinkedHashMap<String, ProtocolModelApi>
     * @see ProtocolModelApi#getCode() key
     * @return
     */
    Map<String, ProtocolModelApi> getApis();

    /**
     * 协议模型属性
     * @see java.util.LinkedHashMap<String, ProtocolModelAttr>
     * @see ProtocolModelAttr#getField() key
     * @return
     */
    Map<String, ProtocolModelAttr> getAttrs();
}
