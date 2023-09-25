package com.iteaj.framework.spi.iot.protocol;

/**
 * 协议模型属性
 */
public class ProtocolModelAttr {

    /**
     * 属性代码
     */
    private String field;

    /**
     * 属性名称
     */
    private String name;

    /**
     * 属性类型
     */
    private String type;

    /**
     * 属性说明
     */
    private String remark;

    public ProtocolModelAttr(String field, String name, String type) {
        this.field = field;
        this.name = name;
        this.type = type;
    }

    public ProtocolModelAttr(String field, String name, String type, String remark) {
        this.field = field;
        this.name = name;
        this.type = type;
        this.remark = remark;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
