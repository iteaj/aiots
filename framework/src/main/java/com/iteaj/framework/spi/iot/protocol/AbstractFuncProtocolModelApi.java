package com.iteaj.framework.spi.iot.protocol;

import com.iteaj.framework.spi.iot.consts.FuncType;

import java.util.Map;

/**
 * 功能协议接口
 */
public abstract class AbstractFuncProtocolModelApi extends AbstractProtocolModelApi{

    private FuncType funcType;

    public AbstractFuncProtocolModelApi(String code, String name, FuncType type) {
        this(code, name, type, null);
    }

    public AbstractFuncProtocolModelApi(String code, String name, FuncType type, String remark) {
        super(code, name, remark, ProtocolApiType.func);
        this.funcType = type;
    }

    public AbstractFuncProtocolModelApi(String code, String name, FuncType type, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        this(code, name, type, null, upConfig, downConfig);
    }

    public AbstractFuncProtocolModelApi(String code, String name, FuncType type, String remark, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, remark, ProtocolApiType.func, upConfig, downConfig);
        this.funcType = type;
    }

    public AbstractFuncProtocolModelApi buildRead() {
        return this.setFuncType(FuncType.read);
    }

    public AbstractFuncProtocolModelApi buildWrite() {
        return this.setFuncType(FuncType.write);
    }

    public FuncType getFuncType() {
        return funcType;
    }

    public AbstractFuncProtocolModelApi setFuncType(FuncType funcType) {
        this.funcType = funcType; return this;
    }
}
