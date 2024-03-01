package com.iteaj.iboot.module.iot.collect.data;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HashMapRealtimeDataService implements RealtimeDataService{

    private Map<String, Map<String, Map<String, RealtimeData>>> realtimeMap = new ConcurrentHashMap();

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
        Map<String, Map<String, RealtimeData>> productMap = realtimeMap.get(productCode);
        if(productMap == null) {
            synchronized (this) {
                productMap = realtimeMap.get(productCode);
                if(productMap == null) {
                    realtimeMap.put(productCode, productMap = new ConcurrentHashMap<>());
                }
            }
        }

        Map<String, RealtimeData> deviceMap = productMap.get(deviceSn);
        if(deviceMap == null) {
            synchronized (this) {
                deviceMap = productMap.get(deviceSn);
                if(deviceMap == null) {
                    productMap.put(deviceSn, deviceMap = new ConcurrentHashMap<>());
                }
            }
        }

        RealtimeData data = deviceMap.get(eventOrSignalField);
        if(data == null) {
            deviceMap.put(eventOrSignalField, new RealtimeData<>(value));
        } else {
            data.setRealtime(value);
        }
    }

    @Override
    public Map<String, RealtimeData> listOfDevice(String productCode, String deviceSn) {
        Map<String, Map<String, RealtimeData>> deviceMap = realtimeMap.get(productCode);
        if(deviceMap != null) {
            return deviceMap.get(deviceSn);
        }

        return Collections.EMPTY_MAP;
    }

    @Override
    public RealtimeData getOfDeviceAndKey(String productCode, String deviceSn, String eventOrSignalField) {
        return this.listOfDevice(productCode, deviceSn).get(eventOrSignalField);
    }
}
