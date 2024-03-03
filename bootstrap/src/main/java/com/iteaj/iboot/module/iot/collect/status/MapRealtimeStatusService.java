package com.iteaj.iboot.module.iot.collect.status;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapRealtimeStatusService implements RealtimeStatusService{

    private Map<String, Map<String, RealtimeStatus>> realtimeMap = new ConcurrentHashMap(500);

    @Override
    public RealtimeStatus get(String protocolCode, String deviceSn) {
        Map<String, RealtimeStatus> realtimeStatusMap = realtimeMap.get(protocolCode);
        if(realtimeStatusMap != null) {
            return realtimeStatusMap.get(deviceSn);
        }

        return null;
    }

    @Override
    public Map<String, RealtimeStatus> get(String protocolCode, String... deviceSn) {
        Map<String, RealtimeStatus> realtimeStatusMap = realtimeMap.get(protocolCode);
        if(realtimeStatusMap != null && deviceSn != null) {
            Map<String, RealtimeStatus> result = new HashMap<>();
            for (int i = 0; i < deviceSn.length; i++) {
                result.put(deviceSn[i], realtimeStatusMap.get(deviceSn[i]));
            }

            return result;
        }

        return Collections.EMPTY_MAP;
    }

    @Override
    public void put(String protocolCode, String deviceSn, RealtimeStatus value) {
        Map<String, RealtimeStatus> realtimeStatusMap = realtimeMap.get(protocolCode);
        if(realtimeStatusMap == null) {
            synchronized (this) {
                realtimeStatusMap = realtimeMap.get(protocolCode);
                if(realtimeStatusMap == null) {
                    realtimeMap.put(protocolCode, realtimeStatusMap = new ConcurrentHashMap<>());
                }
            }
        }

        realtimeStatusMap.put(deviceSn, value);
    }

    @Override
    public RealtimeStatus remove(String protocolCode, String deviceSn) {
        Map<String, RealtimeStatus> realtimeStatusMap = realtimeMap.get(protocolCode);
        if(realtimeStatusMap != null) {
            return realtimeStatusMap.remove(deviceSn);
        }

        return null;
    }

    @Override
    public Map<String, RealtimeStatus> remove(String protocolCode, String... deviceSn) {
        Map<String, RealtimeStatus> realtimeStatusMap = realtimeMap.get(protocolCode);
        if(realtimeStatusMap != null && deviceSn != null) {
            Map<String, RealtimeStatus> result = new HashMap<>();
            for (int i = 0; i < deviceSn.length; i++) {
                result.put(deviceSn[i], realtimeStatusMap.remove(deviceSn[i]));
            }

            return result;
        }

        return Collections.EMPTY_MAP;
    }
}
