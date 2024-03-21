package com.iteaj.iboot.module.iot.cache.data;

import com.iteaj.framework.spi.iot.DeviceKey;
import com.iteaj.framework.spi.iot.SignalOrFieldValue;

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
    public Map<String, RealtimeData> remove(String protocolCode, DeviceKey key) {
        Map<String, Map<String, RealtimeData>> productMap = realtimeMap.get(protocolCode);
        if(productMap != null) {
            return productMap.remove(key.getKey());
        }

        return null;
    }

    @Override
    public RealtimeData remove(String protocolCode, DeviceKey key, String signalOrField) {
        Map<String, RealtimeData> realtimeDataMap = remove(protocolCode, key);
        if(null != realtimeDataMap) {
            return realtimeDataMap.remove(signalOrField);
        }

        return null;
    }

    @Override
    public Map<String, RealtimeData> remove(String protocolCode, DeviceKey key, String... signalOrFields) {
        Map<String, RealtimeData> realtimeDataMap = this.listOfDevice(protocolCode, key);
        if(realtimeDataMap != null && signalOrFields != null) {
            Map<String, RealtimeData> result = new HashMap<>();
            Arrays.stream(signalOrFields).forEach(item -> {
                RealtimeData remove = realtimeDataMap.remove(item);
                result.put(item, remove);
            });

            return result;
        }

        return null;
    }

    @Override
    public void put(String protocolCode, DeviceKey key, String signalOrField, Object value) {
        Map<String, Map<String, RealtimeData>> productMap = realtimeMap.get(protocolCode);
        if(productMap == null) {
            synchronized (this) {
                productMap = realtimeMap.get(protocolCode);
                if(productMap == null) {
                    realtimeMap.put(protocolCode, productMap = new ConcurrentHashMap<>());
                }
            }
        }

        Map<String, RealtimeData> deviceMap = productMap.get(key.getKey());
        if(deviceMap == null) {
            synchronized (this) {
                deviceMap = productMap.get(key.getKey());
                if(deviceMap == null) {
                    productMap.put(key.getKey(), deviceMap = new ConcurrentHashMap<>());
                }
            }
        }

        RealtimeData data = deviceMap.get(signalOrField);
        if(data == null) {
            deviceMap.put(signalOrField, new RealtimeData<>(signalOrField, value));
        } else {
            data.setRealtime(value);
        }
    }

    @Override
    public void put(String protocolCode, DeviceKey key, SignalOrFieldValue value) {
        this.put(protocolCode, key, value.getSignalOrField(), value.getValue());
    }

    @Override
    public Map<String, RealtimeData> listOfDevice(String protocolCode, DeviceKey key) {
        Map<String, Map<String, RealtimeData>> deviceMap = realtimeMap.get(protocolCode);
        if(deviceMap != null) {
            return deviceMap.get(key.getKey());
        }

        return Collections.EMPTY_MAP;
    }

    @Override
    public RealtimeData getOfDeviceAndKey(String protocolCode, DeviceKey key, String signalOrField) {
        return this.listOfDevice(protocolCode, key).get(signalOrField);
    }

    @Override
    public Map<String, RealtimeData> listOfDevice(String protocolCode, DeviceKey key, String... signalOrFields) {
        Map<String, RealtimeData> realtimeDataMap = listOfDevice(protocolCode, key);
        if(null != realtimeDataMap && signalOrFields != null) {
            Map<String, RealtimeData> result = new HashMap<>();
            Arrays.stream(signalOrFields).forEach(item -> {
                result.put(item, realtimeDataMap.get(item));
            });

            return result;
        }

        return Collections.EMPTY_MAP;
    }
}
