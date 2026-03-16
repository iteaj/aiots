package com.iteaj.framework.spi.iot.data;

import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.spi.iot.consts.ConnectionType;
import com.iteaj.framework.spi.iot.consts.DeviceTypeEnum;
import com.iteaj.framework.spi.iot.consts.GatewayType;

import java.util.Date;

/**
 * 产品元数据
 */
public interface ProductMeta {

    /**
     * 产品代码
     * @return 产品代码
     */
    String getCode();

    /**
     * 设备类型
     * @return 设备类型
     */
    DeviceTypeEnum getDeviceType();

    /**
     * 产品类型
     * @return 产品类型id
     */
    Long getProductTypeId();

    /**
     * 类型名称
     * @return 类型名称
     */
    String getTypeName();

    /**
     * 产品名称
     * @return 产品名称
     */
    String getName();

    /**
     * 父产品id
     * @return 父产品id
     */
    Long getParentId();

    /**
     * 绑定的网关
     * @return 网关id
     */
    Long getGatewayId();

    /**
     * 绑定的协议
     * @return 协议id
     */
    Long getProtocolId();

    /**
     * 产品配置
     * @return 产品配置
     */
    JSONObject getConfig();

    /**
     * 产品说明
     * @return 产品说明
     */
    String getRemark();

    /**
     * 创建时间
     * @return 创建时间
     */
    Date getCreateTime();

    /**
     * 网关类型
     * @return 网关类型
     */
    GatewayType getGatewayType();

    /**
     * 网关名称
     * @return 网关名称
     */
    String getGatewayName();

    /**
     * 协议名称
     * @return 协议名称
     */
    String getProtocolName();

    /**
     * 协议代码
     * @return 协议代码
     */
    String getProtocolCode();

    /**
     * 网关连接类型
     * @return 网关连接类型
     */
    ConnectionType getConnectType();

    /**
     * 网关连接类型名称
     * @return 网关连接类型名称
     */
    default String getConnectTypeName() {
        return getConnectType() != null ? getConnectType().getDesc() : null;
    }

    /**
     * 设备类型名称
     * @return 设备类型名称
     */
    default String getDeviceTypeName() {
        return getDeviceType() != null ? getDeviceType().getDesc() : null;
    }

    /**
     * 网关类型名称
     * @return 网关类型名称
     */
    default String getGatewayTypeName() {
        return getGatewayType() != null ? getGatewayType().getDesc() : null;
    }
}
