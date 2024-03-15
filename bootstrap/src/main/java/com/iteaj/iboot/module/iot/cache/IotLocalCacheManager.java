package com.iteaj.iboot.module.iot.cache;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class IotLocalCacheManager implements IotCacheManager {

    private Map<Long, Set<RealtimeStatus>> groupMaps = new ConcurrentHashMap<>(50);
    private Map<String, Map<String, RealtimeStatus>> realtimeMap = new ConcurrentHashMap(500);

    @Override
    public RealtimeStatus cache(RealtimeStatus device) {
        validateParams(device);
        Map<String, RealtimeStatus> deviceMaps = realtimeMap.get(device.getProtocolCode());
        if(deviceMaps == null) {
            synchronized (this) {
                deviceMaps = realtimeMap.get(device.getProtocolCode());
                if(deviceMaps == null) {
                    realtimeMap.put(device.getProtocolCode(), deviceMaps = new ConcurrentHashMap<>());
                    deviceMaps.put(device.buildDeviceKey(), device);
                    if(device.getGroupId() != null) {
                        Set<RealtimeStatus> statuses = groupMaps.get(device.getGroupId());
                        if(statuses == null) {
                            groupMaps.put(device.getGroupId(), statuses = new HashSet<>());
                            statuses.add(device);
                        }
                    }
                }
            }
        } else {
            String deviceKey = device.buildDeviceKey();
            deviceMaps.put(deviceKey, device);
            if(device.getGroupId() != null) {
                Set<RealtimeStatus> statuses = groupMaps.get(device.getGroupId());
                if(statuses == null) {
                    synchronized (this) {
                        statuses = groupMaps.get(device.getGroupId());
                        if(statuses == null) {
                            groupMaps.put(device.getGroupId(), statuses = new HashSet<>());
                            statuses.add(device);
                        }
                    }
                }
            }
        }

        return device;
    }

    @Override
    public RealtimeStatus update(RealtimeStatus oldValue, RealtimeStatus newValue) {
        RealtimeStatus realtimeStatus = removeDevice(oldValue);
        this.cache(newValue);
        return realtimeStatus;
    }

    private void validateParams(RealtimeStatus device) {
        if(null == device) {
            throw new IllegalArgumentException("参数错误");
        }
        if(device.getType() == null) {
            throw new IllegalArgumentException("设备类型[type]必填");
        }
        if(device.getUid() == null) {
            throw new IllegalArgumentException("设备[uid]必填");
        }
        if(StrUtil.isBlank(device.getDeviceSn())) {
            throw new IllegalArgumentException("设备编号[deviceSn]必填");
        }
        if(StrUtil.isBlank(device.getProductCode())) {
            throw new IllegalArgumentException("产品编号[productCode]必填");
        }
        if(StrUtil.isBlank(device.getProtocolCode())) {
            throw new IllegalArgumentException("协议码[protocolCode]必填");
        }
    }

    @Override
    public Set<RealtimeStatus> removeByGroupId(Long groupId) {
        if(groupId == null) {
            throw new IllegalArgumentException("参数错误");
        }

        return groupMaps.remove(groupId);
    }

    @Override
    public RealtimeStatus removeDevice(RealtimeStatus device) {
        if(null == device) {
            throw new IllegalArgumentException("参数错误");
        }
        if(StrUtil.isBlank(device.getDeviceSn())) {
            throw new IllegalArgumentException("设备编号[deviceSn]必填");
        }
        if(StrUtil.isBlank(device.getProtocolCode())) {
            throw new IllegalArgumentException("协议码[protocolCode]必填");
        }

        if(StrUtil.isBlank(device.getParentDeviceSn())) {
            return this.removeByKey(device.getProtocolCode(), device.getDeviceSn());
        } else {
            return this.removeByKey(device.getProtocolCode(), device.getDeviceSn(), device.getParentDeviceSn());
        }
    }

    @Override
    public RealtimeStatus removeByKey(String protocolCode, String deviceSn) {
        Map<String, RealtimeStatus> statusMap = realtimeMap.get(protocolCode);
        if(statusMap != null) {
            RealtimeStatus remove = statusMap.remove(deviceSn);
            if(remove != null && remove.getGroupId() != null) {
                Set<RealtimeStatus> statuses = groupMaps.get(remove.getGroupId());
                if(statuses != null) {
                    statuses.remove(remove);
                }
            }

            return remove;
        }

        return null;
    }

    @Override
    public RealtimeStatus removeByKey(String protocolCode, String deviceSn, String parentDeviceSn) {
        Map<String, RealtimeStatus> statusMap = realtimeMap.get(protocolCode);
        if(statusMap != null) {
            RealtimeStatus remove = statusMap.remove(RealtimeStatus.buildDeviceKey(deviceSn, parentDeviceSn));
            if(remove != null && remove.getGroupId() != null) {
                Set<RealtimeStatus> statuses = groupMaps.get(remove.getGroupId());
                if(statuses != null) {
                    statuses.remove(remove);
                }
            }

            return remove;
        }

        return null;
    }

    @Override
    public RealtimeStatus updateStatus(String protocolCode, String deviceSn, DeviceStatus status) {
        Map<String, RealtimeStatus> statusMap = realtimeMap.get(protocolCode);
        if(statusMap != null) {
            RealtimeStatus realtimeStatus = statusMap.get(deviceSn);
            if(realtimeStatus != null) {
                realtimeStatus.setStatus(status);
            }

            return realtimeStatus;
        }
        return null;
    }

    @Override
    public RealtimeStatus updateStatus(String protocolCode, String deviceSn, String parentDeviceSn, DeviceStatus status) {
        Map<String, RealtimeStatus> statusMap = realtimeMap.get(protocolCode);
        if(statusMap != null) {
            RealtimeStatus realtimeStatus = statusMap.get(RealtimeStatus.buildDeviceKey(deviceSn, parentDeviceSn));
            if(realtimeStatus != null) {
                realtimeStatus.setStatus(status);
                realtimeStatus.setUpdateTime(new Date());
            }

            return realtimeStatus;
        }

        return null;
    }

    @Override
    public RealtimeStatus get(String protocolCode, String deviceSn) {
        if(StrUtil.isBlank(protocolCode)) {
            throw new IllegalArgumentException("参数[protocolCode]必填");
        }
        if(StrUtil.isBlank(deviceSn)) {
            throw new IllegalArgumentException("参数[deviceSn]必填");
        }
        Map<String, RealtimeStatus> statusMap = realtimeMap.get(protocolCode);
        if(statusMap != null) {
            return statusMap.get(deviceSn);
        }

        return null;
    }

    @Override
    public RealtimeStatus get(String protocolCode, String deviceSn, String parentDeviceSn) {
        if(StrUtil.isBlank(protocolCode)) {
            throw new IllegalArgumentException("参数[protocolCode]必填");
        }
        if(StrUtil.isBlank(deviceSn)) {
            throw new IllegalArgumentException("参数[deviceSn]必填");
        }
        if(StrUtil.isBlank(parentDeviceSn)) {
            throw new IllegalArgumentException("参数[parentDeviceSn]必填");
        }

        Map<String, RealtimeStatus> statusMap = realtimeMap.get(protocolCode);
        if(statusMap != null) {
            return statusMap.get(RealtimeStatus.buildDeviceKey(deviceSn, parentDeviceSn));
        }

        return null;
    }

    @Override
    public Map<String, RealtimeStatus> list(String protocolCode, String... deviceSn) {
        if(StrUtil.isBlank(protocolCode)) {
            throw new IllegalArgumentException("参数[protocolCode]必填");
        }

        if(ArrayUtil.isEmpty(deviceSn)) {
            throw new IllegalArgumentException("未指定参数[deviceSn[]]");
        }

        Map<String, RealtimeStatus> result = new HashMap<>();
        Map<String, RealtimeStatus> statusMap = realtimeMap.get(protocolCode);
        if(statusMap != null) {
            for (int i = 0; i < deviceSn.length; i++) {
                String key = deviceSn[i];
                result.put(key, statusMap.get(key));
            }
        }

        return result;
    }

    @Override
    public Map<String, RealtimeStatus> listForParent(String protocolCode, String parentDeviceSn, String... deviceSn) {
        if(StrUtil.isBlank(protocolCode)) {
            throw new IllegalArgumentException("参数[protocolCode]必填");
        }
        if(StrUtil.isBlank(parentDeviceSn)) {
            throw new IllegalArgumentException("未指定参数[parentDeviceSn]");
        }
        if(ArrayUtil.isEmpty(deviceSn)) {
            throw new IllegalArgumentException("未指定参数[deviceSn[]]");
        }
        Map<String, RealtimeStatus> result = new HashMap<>();
        Map<String, RealtimeStatus> statusMap = realtimeMap.get(protocolCode);
        if(statusMap != null) {
            for (int i = 0; i < deviceSn.length; i++) {
                String key = deviceSn[i];
                result.put(key, statusMap.get(RealtimeStatus.buildDeviceKey(key, parentDeviceSn)));
            }
        }

        return result;
    }

    @Override
    public Set<RealtimeStatus> listByGroupId(Long id) {
        if(id == null) {
            return Collections.emptySet();
        }

        Set<RealtimeStatus> statusSet = groupMaps.get(id);
        return statusSet != null ? statusSet : Collections.emptySet();
    }

}
