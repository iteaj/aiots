package com.iteaj.framework.spi.iot.protocol;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 协议模型接口
 */
public abstract class AbstractProtocolModelApi implements ProtocolModelApi{

    /**
     * 接口代码
     */
    private String code;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 接口说明
     */
    private String remark;

    /**
     * 接口类型
     */
    private ProtocolApiType type;

    /**
     * 上行接口配置
     * @see java.util.LinkedHashMap<String, ProtocolModelApiConfig>
     * @see ProtocolModelApiConfig#getProtocolModelAttrField()
     */
    private Map<String, ProtocolModelApiConfig> upConfig;

    /**
     * 下行接口配置
     * @see java.util.LinkedHashMap<String, ProtocolModelApiConfig>
     * @see ProtocolModelApiConfig#getProtocolModelAttrField()
     */
    private Map<String, ProtocolModelApiConfig> downConfig;

    public AbstractProtocolModelApi(String code, String name, ProtocolApiType type) {
        this(code, name, type, new ProtocolLinkedMap<>(), new ProtocolLinkedMap<>());
    }

    public AbstractProtocolModelApi(String code, String name, String remark, ProtocolApiType type) {
        this(code, name, remark, type, new ProtocolLinkedMap<>(), new ProtocolLinkedMap<>());
    }

    public AbstractProtocolModelApi(String code, String name, ProtocolApiType type, Map<String
            , ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.upConfig = upConfig;
        this.downConfig = downConfig;
    }

    public AbstractProtocolModelApi(String code, String name, String remark, ProtocolApiType type
            , Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.remark = remark;
        this.upConfig = upConfig;
        this.downConfig = downConfig;
    }

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public ProtocolApiType getType() {
        return type;
    }

    public void setType(ProtocolApiType type) {
        this.type = type;
    }

    public Map<String, ProtocolModelApiConfig> getUpConfig() {
        return upConfig;
    }

    public void setUpConfig(Map<String, ProtocolModelApiConfig> upConfig) {
        this.upConfig = upConfig;
    }

    public Map<String, ProtocolModelApiConfig> getDownConfig() {
        return downConfig;
    }

    public void setDownConfig(Map<String, ProtocolModelApiConfig> downConfig) {
        this.downConfig = downConfig;
    }
}
