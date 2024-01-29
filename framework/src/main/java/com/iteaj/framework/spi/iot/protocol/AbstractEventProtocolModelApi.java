package com.iteaj.framework.spi.iot.protocol;

import com.iteaj.framework.spi.iot.consts.ApiEventType;

import java.util.Map;

/**
 * 事件协议接口
 */
public abstract class AbstractEventProtocolModelApi extends AbstractProtocolModelApi {

    private ApiEventType eventType;

    public AbstractEventProtocolModelApi(String code, String name, ApiEventType eventType) {
        this(code, name, null, eventType);
    }

    public AbstractEventProtocolModelApi(String code, String name, String remark, ApiEventType eventType) {
        super(code, name, remark, ProtocolApiType.event);
        this.eventType = eventType;
    }

    public AbstractEventProtocolModelApi(String code, String name, ApiEventType eventType, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        this(code, name, null, eventType, upConfig, downConfig);
    }

    public AbstractEventProtocolModelApi(String code, String name, String remark, ApiEventType eventType, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, remark, ProtocolApiType.event, upConfig, downConfig);
        this.eventType = eventType;
    }

    public ApiEventType getEventType() {
        return eventType;
    }

    public void setEventType(ApiEventType eventType) {
        this.eventType = eventType;
    }
}
