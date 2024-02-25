package com.iteaj.framework.spi.iot.protocol;

import com.iteaj.framework.spi.iot.consts.EventTriggerMode;

import java.util.Map;

/**
 * 事件协议接口
 */
public abstract class AbstractEventProtocolModelApi extends AbstractProtocolModelApi {

    private EventTriggerMode triggerMode;

    public AbstractEventProtocolModelApi(String code, String name, EventTriggerMode triggerMode) {
        this(code, name, null, triggerMode);
    }

    public AbstractEventProtocolModelApi(String code, String name, String remark, EventTriggerMode triggerMode) {
        super(code, name, remark, ProtocolApiType.event);
        this.triggerMode = triggerMode;
    }

    public AbstractEventProtocolModelApi(String code, String name, EventTriggerMode triggerMode, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        this(code, name, null, triggerMode, upConfig, downConfig);
    }

    public AbstractEventProtocolModelApi(String code, String name, String remark, EventTriggerMode triggerMode, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, remark, ProtocolApiType.event, upConfig, downConfig);
        this.triggerMode = triggerMode;
    }

    public EventTriggerMode getTriggerMode() {
        return triggerMode;
    }

    public void setTriggerMode(EventTriggerMode triggerMode) {
        this.triggerMode = triggerMode;
    }
}
