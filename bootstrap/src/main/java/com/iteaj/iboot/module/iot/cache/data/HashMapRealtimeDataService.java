package com.iteaj.iboot.module.iot.cache.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HashMapRealtimeDataService implements RealtimeDataService{

    private Map<String, Map<String, Map<String, RealtimeData>>> realtimeMap = new ConcurrentHashMap(500);

    @Override
    public void remove(String protocolCode) {
        realtimeMap.remove(protocolCode);
    }

    @Override
    public Map<String, RealtimeData> remove(String protocolCode, String deviceSn) {
        Map<String, Map<String, RealtimeData>> productMap = realtimeMap.get(protocolCode);
        if(productMap != null) {
            return productMap.remove(deviceSn);
        }

        return null;
    }

    @Override
    public RealtimeData remove(String protocolCode, String deviceSn, String eventOrSignalField) {
        Map<String, RealtimeData> realtimeDataMap = remove(protocolCode, deviceSn);
        if(null != realtimeDataMap) {
            return realtimeDataMap.remove(eventOrSignalField);
        }

        return null;
    }

    @Override
    public Map<String, RealtimeData> remove(String protocolCode, String deviceSn, String... eventOrSignalFields) {
        Map<String, RealtimeData> realtimeDataMap = this.listOfDevice(protocolCode, deviceSn);
        if(realtimeDataMap != null && eventOrSignalFields != null) {
            Map<String, RealtimeData> result = new HashMap<>();
            Arrays.stream(eventOrSignalFields).forEach(item -> {
                RealtimeData remove = realtimeDataMap.remove(item);
                result.put(item, remove);
            });

            return result;
        }

        return null;
    }

    @Override
    public void put(String protocolCode, String deviceSn, String eventOrSignalField, Object value) {
        Map<String, Map<String, RealtimeData>> productMap = realtimeMap.get(protocolCode);
        if(productMap == null) {
            synchronized (this) {
                productMap = realtimeMap.get(protocolCode);
                if(productMap == null) {
                    realtimeMap.put(protocolCode, productMap = new ConcurrentHashMap<>());
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
            deviceMap.put(eventOrSignalField, new RealtimeData<>(eventOrSignalField, value));
        } else {
            data.setRealtime(value);
        }
    }

    @Override
    public Map<String, RealtimeData> listOfDevice(String protocolCode, String deviceSn) {
        Map<String, Map<String, RealtimeData>> deviceMap = realtimeMap.get(protocolCode);
        if(deviceMap != null) {
            return deviceMap.get(deviceSn);
        }

        return Collections.EMPTY_MAP;
    }

    @Override
    public RealtimeData getOfDeviceAndKey(String protocolCode, String deviceSn, String eventOrSignalField) {
        return this.listOfDevice(protocolCode, deviceSn).get(eventOrSignalField);
    }

    @Override
    public Map<String, RealtimeData> listOfDevice(String protocolCode, String deviceSn, String... eventOrSignalFields) {
        Map<String, RealtimeData> realtimeDataMap = listOfDevice(protocolCode, deviceSn);
        if(null != realtimeDataMap && eventOrSignalFields != null) {
            Map<String, RealtimeData> result = new HashMap<>();
            Arrays.stream(eventOrSignalFields).forEach(item -> {
                result.put(item, realtimeDataMap.get(item));
            });

            return result;
        }

        return Collections.EMPTY_MAP;
    }
}
