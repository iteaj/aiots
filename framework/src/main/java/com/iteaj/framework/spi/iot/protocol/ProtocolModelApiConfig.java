package com.iteaj.framework.spi.iot.protocol;

import com.iteaj.framework.spi.iot.consts.ApiConfigDirection;

/**
 * 协议模型接口配置
 */
public class ProtocolModelApiConfig {

    /**
     * 排序
     */
    private int sort;

    /**
     * 长度
     */
    private int length;

    /**
     * 说明
     */
    private String remark;

    /**
     * 点位地址
     */
    private String position;

    /**
     * 属性方向
     */
    private ApiConfigDirection direction;

    /**
     * 属性代码
     */
    private String protocolModelAttrField;

    public ProtocolModelApiConfig(String remark, String protocolModelAttrField) {
        this.remark = remark;
        this.protocolModelAttrField = protocolModelAttrField;
    }

    public ProtocolModelApiConfig(String protocolModelAttrField, String remark, ApiConfigDirection direction) {
        this.remark = remark;
        this.direction = direction;
        this.protocolModelAttrField = protocolModelAttrField;
    }

    public ProtocolModelApiConfig(int length, String remark, String protocolModelAttrField) {
        this.length = length;
        this.remark = remark;
        this.protocolModelAttrField = protocolModelAttrField;
    }

    public ProtocolModelApiConfig(int sort, int length, String remark, String position, String protocolModelAttrField) {
        this.sort = sort;
        this.length = length;
        this.remark = remark;
        this.position = position;
        this.protocolModelAttrField = protocolModelAttrField;
    }

    public static ProtocolModelApiConfig upBuild(String attrField, String remark) {
        return new ProtocolModelApiConfig(attrField, remark, ApiConfigDirection.UP);
    }

    public static ProtocolModelApiConfig downBuild(String attrField, String remark) {
        return new ProtocolModelApiConfig(attrField, remark, ApiConfigDirection.DOWN);
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getProtocolModelAttrField() {
        return protocolModelAttrField;
    }

    public void setProtocolModelAttrField(String protocolModelAttrField) {
        this.protocolModelAttrField = protocolModelAttrField;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public ApiConfigDirection getDirection() {
        return direction;
    }

    public void setDirection(ApiConfigDirection direction) {
        this.direction = direction;
    }
}
