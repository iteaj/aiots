package com.iteaj.iboot.plugin.ai.mcp.dto;

import com.alibaba.fastjson2.JSONObject;
import com.iteaj.framework.spi.iot.consts.ConnectionType;
import com.iteaj.framework.spi.iot.consts.DeviceTypeEnum;
import com.iteaj.framework.spi.iot.consts.FuncStatusEnum;
import com.iteaj.framework.spi.iot.consts.GatewayType;
import com.iteaj.framework.spi.iot.data.ProductMeta;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

@Setter
@Accessors(chain=true)
public class ProductAiDto implements ProductMeta {

    private String code;
    private DeviceTypeEnum deviceType;
    private Long productTypeId;
    private String typeName;
    private String name;
    private Long parentId;
    private Long gatewayId;
    private Long protocolId;
    private JSONObject config;
    private String remark;
    private Date createTime;
    private GatewayType gatewayType;
    private String gatewayName;
    private String protocolName;
    private String protocolCode;
    private ConnectionType connectType;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public FuncStatusEnum getStatus() {
        return null;
    }

    @Override
    public DeviceTypeEnum getDeviceType() {
        return deviceType;
    }

    @Override
    public Long getProductTypeId() {
        return productTypeId;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Long getParentId() {
        return parentId;
    }

    @Override
    public Long getGatewayId() {
        return gatewayId;
    }

    @Override
    public Long getProtocolId() {
        return protocolId;
    }

    @Override
    public JSONObject getConfig() {
        return config;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public GatewayType getGatewayType() {
        return gatewayType;
    }

    @Override
    public String getGatewayName() {
        return gatewayName;
    }

    @Override
    public String getProtocolName() {
        return protocolName;
    }

    @Override
    public String getProtocolCode() {
        return protocolCode;
    }

    @Override
    public ConnectionType getConnectType() {
        return connectType;
    }
}