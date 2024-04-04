package com.iteaj.iboot.module.iot.collect.websocket;

import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class PushData {

    private String type;

    private Object value;

    protected PushData(String type, Object value) {
        this.type = type;
        this.value = value;
    }

    public static PushData buildStatus(RealtimeStatus status) {
        return new PushData("status", status);
    }

    public static PushData buildModel(Map<String, SignalOrFieldValue> value) {
        return new PushData("model", value);
    }

    public static PushData buildSignal(Map<String, SignalOrFieldValue> value) {
        return new PushData("signal", value);
    }
}
