package com.iteaj.iboot.module.iot.collect.status;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.utils.RedisUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RedisRealtimeStatusService implements RealtimeStatusService{

    private static final String KEY_PREFIX = "Realtime:Status:";

    @Override
    public RealtimeStatus get(String protocolCode, String deviceSn) {
        return (RealtimeStatus) RedisUtils.getOpsForHash().get(KEY_PREFIX + protocolCode, deviceSn);
    }

    @Override
    public Map<String, RealtimeStatus> get(String protocolCode, String... deviceSn) {
        List<RealtimeStatus> list = RedisUtils.getOpsForHash().multiGet(KEY_PREFIX + protocolCode, Arrays.asList(deviceSn));
        if(CollectionUtil.isNotEmpty(list)) {
            return list.stream().collect(Collectors.toMap(RealtimeStatus::getDeviceSn, item -> item));
        }

        return Collections.EMPTY_MAP;
    }

    @Override
    public void put(String protocolCode, String deviceSn, RealtimeStatus value) {
        RedisUtils.getOpsForHash().put(KEY_PREFIX + protocolCode, deviceSn, value);
    }

    @Override
    public RealtimeStatus remove(String protocolCode, String deviceSn) {
        RealtimeStatus realtimeStatus = get(protocolCode, deviceSn);
        if(realtimeStatus != null) {
            RedisUtils.getOpsForHash().delete(KEY_PREFIX + protocolCode, deviceSn);
        }

        return realtimeStatus;
    }

    @Override
    public Map<String, RealtimeStatus> remove(String protocolCode, String... deviceSn) {
        List<RealtimeStatus> list = RedisUtils.getOpsForHash().multiGet(KEY_PREFIX + protocolCode, Arrays.asList(deviceSn));
        if(CollectionUtil.isNotEmpty(list)) {
            RedisUtils.getOpsForHash().delete(KEY_PREFIX + protocolCode, deviceSn);
            return list.stream().collect(Collectors.toMap(RealtimeStatus::getDeviceSn, item -> item));
        }

        return null;
    }
}
