package com.iteaj.framework.spi.iot.listener;

/**
 * 实时负载
 * @see IotEventType#Realtime
 * @see RealtimeInfoListener
 */
public class RealtimePayload extends JsonPayload {

    /**
     * 实时负载类型
     * warn 实时告警
     * status 实时设备在线状态
     * data 实时采集的数据
     */
    private String type;

    public RealtimePayload(String type, Object value) {
        super(value);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
