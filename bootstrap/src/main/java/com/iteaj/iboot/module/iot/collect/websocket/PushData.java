package com.iteaj.iboot.module.iot.collect.websocket;

import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.framework.spi.iot.consts.CollectStatus;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class PushData {

    private String uid;

    private String type;

    private Object value;

    protected PushData(String uid, String type, Object value) {
        this.uid = uid;
        this.type = type;
        this.value = value;
    }

    public static PushData build(String uid, String type, Object value) {
        return new PushData(uid, type, value);
    }

    public static PushData buildStatus(RealtimeStatus status) {
        return new PushData(status.getUid(), "status", status);
    }

    public static PushData buildModel(String uid, Map<String, ModelAttrData> value) {
        return new PushData(uid, "model", value);
    }

    public static PushData buildSignal(String uid, Map<String, SignalOrFieldValue> value) {
        return new PushData(uid, "signal", value);
    }

    public static ModelAttrData of(SignalOrFieldValue value) {
        ModelAttrData data = new ModelAttrData();
        data.setValue(value.getValue());
        data.setCollectTime(value.getCollectTime());
        data.setStatus(value.getStatus());
        return data;
    }

    @Getter
    @Setter
    public static class ModelAttrData {
        /**
         * 值
         */
        private Object value;

        /**
         * 采集时间
         */
        private Date collectTime;

        /**
         * 采集状态
         */
        private CollectStatus status;

        private String grade;

        private String indicator;
    }
}
