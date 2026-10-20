package com.iteaj.framework.spi.iot.listener;

import java.util.Collection;
import java.util.Objects;

/**
 * 实时负载
 * @see IotEventType#Realtime
 * @see RealtimeInfoListener
 */
public class RealtimePayload extends EntityPayload<Collection> {

    String uid;

    /**
     * 实时负载类型
     * warn 实时告警
     * status 实时设备在线状态
     * data 实时采集的数据
     * data_cmd 指令采集的数据
     * data_active 设备主动上报
     */
    private String type;

    public final static String TYPE_DATA_CMD = "data_cmd";
    public final static String TYPE_DATA_ACTIVE = "data_active";

    public RealtimePayload(String type, String uid, Collection values) {
        super(values);
        this.uid = uid;
        this.type = type;
    }

    public boolean isWarn() {
        return Objects.equals(getType(), "warn");
    }

    public boolean isData() {
        return Objects.equals(getType(), "data");
    }

    public boolean isStatus() {
        return Objects.equals(getType(), "status");
    }

    public boolean isDataCmd() {
        return Objects.equals(getType(), "data_cmd");
    }

    public boolean isDataFull() {
        return isData();
    }

    public boolean isDataActive() {
        return Objects.equals(getType(), "data_active");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
