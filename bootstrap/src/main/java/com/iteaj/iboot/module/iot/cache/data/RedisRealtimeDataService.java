package com.iteaj.iboot.module.iot.cache.data;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.utils.RedisUtils;

import java.util.*;
import java.util.stream.Collectors;

public class RedisRealtimeDataService implements RealtimeDataService{

    private static final String KEY_PREFIX = "Realtime:Data:";

    @Override
    public void remove(String protocolCode) {

    }

    @Override
    public Map<String, RealtimeData> remove(String protocolCode, String deviceSn) {
        String key = KEY_PREFIX + protocolCode + ":" + deviceSn;
        List<RealtimeData> values = RedisUtils.getOpsForHash().values(key);
        if(CollectionUtil.isNotEmpty(values)) {
            RedisUtils.getTemplate().delete(key);
            return values.stream().collect(Collectors.toMap(RealtimeData::getEventOrSignalField, item -> item));
        }

        return null;
    }

    @Override
    public RealtimeData remove(String protocolCode, String deviceSn, String eventOrSignalField) {
        String key = KEY_PREFIX + protocolCode + ":" + deviceSn;
        Object o = RedisUtils.getOpsForHash().get(key, eventOrSignalField);
        if(o instanceof RealtimeData) {
            RedisUtils.getOpsForHash().delete(key, eventOrSignalField);
        }

        return (RealtimeData) o;
    }

    @Override
    public Map<String, RealtimeData> remove(String protocolCode, String deviceSn, String... eventOrSignalFields) {
        String key = KEY_PREFIX + protocolCode + ":" + deviceSn;
        List<RealtimeData> values = RedisUtils.getOpsForHash().multiGet(key, Arrays.asList(eventOrSignalFields));
        if(CollectionUtil.isNotEmpty(values)) {
            RedisUtils.getOpsForHash().delete(key, eventOrSignalFields);
            return values.stream().collect(Collectors.toMap(RealtimeData::getEventOrSignalField, item -> item));
        }

        return null;
    }

    @Override
    public void put(String protocolCode, String deviceSn, String eventOrSignalField, Object value) {
        String key = KEY_PREFIX + protocolCode + ":" + deviceSn;
        Object o = RedisUtils.getOpsForHash().get(key, eventOrSignalField);
        if(o instanceof RealtimeData) {
            ((RealtimeData) o).setRealtime(value);
            RedisUtils.getOpsForHash().put(key, eventOrSignalField, o);
        } else {
            RedisUtils.getOpsForHash().put(key
                    , eventOrSignalField, new RealtimeData<>(eventOrSignalField, value));
        }
    }

    @Override
    public Map<String, RealtimeData> listOfDevice(String protocolCode, String deviceSn) {
        Map entries = RedisUtils.getOpsForHash().entries(KEY_PREFIX + protocolCode + ":" + deviceSn);
        return entries;
    }

    @Override
    public RealtimeData getOfDeviceAndKey(String protocolCode, String deviceSn, String eventOrSignalField) {
        return (RealtimeData) RedisUtils.getOpsForHash().get(KEY_PREFIX + protocolCode + ":" + deviceSn, eventOrSignalField);
    }

    @Override
    public Map<String, RealtimeData> listOfDevice(String protocolCode, String deviceSn, String... eventOrSignalFields) {
        List<RealtimeData> list = RedisUtils.getOpsForHash().multiGet(KEY_PREFIX + protocolCode + ":" + deviceSn, Arrays.asList(eventOrSignalFields));
        if(CollectionUtil.isNotEmpty(list)) {
            return list.stream().collect(Collectors.toMap(RealtimeData::getEventOrSignalField, item -> item));
        }

        return Collections.EMPTY_MAP;
    }
}
