package com.iteaj.iboot.module.iot.collect.data;

import com.iteaj.framework.utils.RedisUtils;

import java.util.Map;

public class RedisRealtimeDataService implements RealtimeDataService{

    @Override
    public void remove(String productCode) {

    }

    @Override
    public Map<String, RealtimeData> remove(String productCode, String deviceSn) {
        return null;
    }

    @Override
    public Map<String, RealtimeData> remove(String productCode, String deviceSn, String eventOrSignalField) {
        return null;
    }

    @Override
    public void put(String productCode, String deviceSn, String eventOrSignalField, Object value) {
        Object o = RedisUtils.getOpsForHash().get(productCode + ":" + deviceSn, eventOrSignalField);
        if(o instanceof RealtimeData) {
            ((RealtimeData) o).setRealtime(value);
        } else {
            RedisUtils.getOpsForHash().put(productCode + ":" + deviceSn, eventOrSignalField, new RealtimeData<>(value));
        }
    }

    @Override
    public Map<String, RealtimeData> listOfDevice(String productCode, String deviceSn) {
        Map entries = RedisUtils.getOpsForHash().entries(productCode + ":" + deviceSn);
        return entries;
    }

    @Override
    public RealtimeData getOfDeviceAndKey(String productCode, String deviceSn, String eventOrSignalField) {
        return (RealtimeData) RedisUtils.getOpsForHash().get(productCode + ":" + deviceSn, eventOrSignalField);
    }
}
