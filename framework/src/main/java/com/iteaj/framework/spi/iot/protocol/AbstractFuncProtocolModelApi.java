package com.iteaj.framework.spi.iot.protocol;

import java.util.Map;

/**
 * 功能协议接口
 */
public abstract class AbstractFuncProtocolModelApi extends AbstractProtocolModelApi{

    public AbstractFuncProtocolModelApi(String code, String name) {
        this(code, name, null);
    }

    public AbstractFuncProtocolModelApi(String code, String name, String remark) {
        super(code, name, remark, ProtocolApiType.func);
    }

    public AbstractFuncProtocolModelApi(String code, String name, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        this(code, name, null, upConfig, downConfig);
    }

    public AbstractFuncProtocolModelApi(String code, String name, String remark, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, remark, ProtocolApiType.func, upConfig, downConfig);
    }

}
