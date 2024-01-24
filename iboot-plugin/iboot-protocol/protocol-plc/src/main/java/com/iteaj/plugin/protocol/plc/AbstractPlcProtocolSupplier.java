package com.iteaj.plugin.protocol.plc;

import com.iteaj.framework.spi.iot.NetworkConfigImpl;
import com.iteaj.framework.spi.iot.StaticProtocolSupplier;
import com.iteaj.framework.spi.iot.protocol.AbstractProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelAttr;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractPlcProtocolSupplier<C extends NetworkConfigImpl> extends StaticProtocolSupplier<C> {

    private Map<String, ProtocolModelAttr> modelAttrs = new LinkedHashMap<>();
    private Map<String, AbstractProtocolModelApi> modelApis = new LinkedHashMap<>();

    public Map<String, ProtocolModelAttr> getModelAttrs() {
        return modelAttrs;
    }

    public void setModelAttrs(Map<String, ProtocolModelAttr> modelAttrs) {
        this.modelAttrs = modelAttrs;
    }

    public Map<String, AbstractProtocolModelApi> getModelApis() {
        return modelApis;
    }

    public void setModelApis(Map<String, AbstractProtocolModelApi> modelApis) {
        this.modelApis = modelApis;
    }
}
