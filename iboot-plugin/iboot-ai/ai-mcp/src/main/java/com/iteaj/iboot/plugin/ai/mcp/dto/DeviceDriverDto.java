package com.iteaj.iboot.plugin.ai.mcp.dto;

import com.iteaj.framework.spi.iot.consts.ConnectionType;
import com.iteaj.framework.spi.iot.consts.GatewayType;
import com.iteaj.framework.spi.iot.consts.TransportProtocol;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceDriverDto {

    /**
     * 协议名称
     */
    private String name;

    /**
     * 协议代码
     */
    private String code;

    /**
     * 协议版本
     */
    private String version;

    /**
     * 协议类型
     */
    private TransportProtocol type;

    /**
     * 网关类型
     */
    private GatewayType gatewayType;

    /**
     * 连接类型
     */
    private ConnectionType connectionType;

    /**
     * 协议备注
     */
    private String remark;
}
