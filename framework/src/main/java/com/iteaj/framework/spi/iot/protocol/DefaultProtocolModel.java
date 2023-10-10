package com.iteaj.framework.spi.iot.protocol;

import com.iteaj.framework.spi.iot.consts.CtrlMode;
import com.iteaj.framework.spi.iot.consts.TransportProtocol;

import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultProtocolModel implements ProtocolModel{

    private String code;
    private CtrlMode ctrlMode;
    private TransportProtocol type;

    private Map<String, AbstractProtocolModelApi> modelApis;
    private Map<String, ProtocolModelAttr> modelAttrs;

    public DefaultProtocolModel(String code, CtrlMode ctrlMode, TransportProtocol type) {
        this(code, ctrlMode, type, new LinkedHashMap<>(), new LinkedHashMap<>());
    }

    public DefaultProtocolModel(String code, CtrlMode ctrlMode, TransportProtocol type
            , Map<String, AbstractProtocolModelApi> modelApis, Map<String, ProtocolModelAttr> modelAttrs) {
        this.code = code;
        this.ctrlMode = ctrlMode;
        this.type = type;
        this.modelApis = modelApis;
        this.modelAttrs = modelAttrs;
    }

    public ProtocolModelAttr getModelAttr(String field) {
        return this.modelAttrs.get(field);
    }

    public DefaultProtocolModel addModelAttr(ProtocolModelAttr modelAttr) {
        this.modelAttrs.put(modelAttr.getField(), modelAttr);
        return this;
    }

    public AbstractProtocolModelApi getModelApi(String code) {
        return this.modelApis.get(code);
    }

    public DefaultProtocolModel addModelApi(AbstractProtocolModelApi modelApi) {
        this.modelApis.put(modelApi.getCode(), modelApi);
        modelApi.setProtocolModel(this);
        return this;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getRemark() {
        return null;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCtrlMode(CtrlMode ctrlMode) {
        this.ctrlMode = ctrlMode;
    }

    public void setType(TransportProtocol type) {
        this.type = type;
    }

    @Override
    public CtrlMode getCtrlMode() {
        return this.ctrlMode;
    }

    @Override
    public TransportProtocol getType() {
        return this.type;
    }

    @Override
    public Map<String, AbstractProtocolModelApi> getApis() {
        return this.modelApis;
    }

    @Override
    public Map<String, ProtocolModelAttr> getAttrs() {
        return this.modelAttrs;
    }
}
