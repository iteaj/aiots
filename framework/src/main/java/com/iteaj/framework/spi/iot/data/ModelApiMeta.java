package com.iteaj.framework.spi.iot.data;

import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;

import java.util.List;

public interface ModelApiMeta {

    /**
     * 主键ID
     */
    Long getId();

    /**
     * 所属产品ID
     */
    Long getProductId();

    /**
     * 产品代码
     */
    String getProductCode();

    /**
     * 接口代码
     */
    String getCode();

    /**
     * 接口名称
     */
    String getName();

    /**
     * 接口类型（func/event）
     */
    ProtocolApiType getType();

    /**
     * 功能类型（R/W/RW）
     */
    FuncType getFuncType();

    /**
     * 事件触发方式
     */
    TriggerMode getTriggerMode();

    /**
     * 协议指令
     */
    String getDirect();

    /**
     * 接口状态
     */
    String getStatus();

    /**
     * 接口说明
     */
    String getRemark();

    /**
     * 是否显示到调试
     */
    Boolean getDebug();

    /**
     * 是否为协议接口
     */
    Boolean getProtocol();

    /**
     * 是否作为状态位（用于点位控制协议）
     */
    Boolean getAsStatus();

    List<? extends ModelDirectMeta> resolverCommand(String uid, List<? extends ModelAttrMeta> attrs);
}
