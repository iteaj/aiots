package com.iteaj.iboot.module.iot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.iteaj.framework.spi.iot.data.IndicatorStats;
import com.iteaj.framework.spi.iot.data.RealtimeCollectData;
import com.iteaj.framework.spi.iot.data.RealtimeCollectDataService;
import com.iteaj.iboot.module.iot.dto.AssociatedStats;
import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.iteaj.iboot.module.iot.service.IDataIndicatorService;
import com.iteaj.iboot.module.iot.service.IDeviceGroupService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 数据指标服务实现
 */
@Service
public class DataIndicatorServiceImpl implements IDataIndicatorService {

    private final RealtimeCollectDataService collectDataService;
    private final IDeviceGroupService deviceGroupService;

    public DataIndicatorServiceImpl(RealtimeCollectDataService collectDataService, IDeviceGroupService deviceGroupService) {
        this.collectDataService = collectDataService;
        this.deviceGroupService = deviceGroupService;
    }

    @Override
    public IndicatorStats getStats(String uid, Long fieldId, String start, String end) {
        return collectDataService.statsOfField(uid, fieldId, start, end);
    }

    @Override
    public List<AssociatedStats> listData(String uid, Long fieldId, String start, String end) {
        List<RealtimeCollectData> collectData = collectDataService.listOfData(uid, fieldId, start, end);
        return resolverDeviceGroupName(collectData.stream().map(AssociatedStats::of).toList());
    }

    @Override
    public List<AssociatedStats> associatedPoints(String uid, Long excludeFieldId, String collectTime) {
        List<RealtimeCollectData> collectData = collectDataService.associatedPoints(uid, excludeFieldId, collectTime);
        return resolverDeviceGroupName(collectData.stream().map(AssociatedStats::of).toList());
    }

    @Override
    public List<AssociatedStats> crossDevicePoints(String productCode, Long fieldId, String collectTime, String excludeUid) {
        List<RealtimeCollectData> collectData = collectDataService.crossDevicePoints(productCode, fieldId, collectTime, excludeUid);
        return resolverDeviceGroupName(collectData.stream().map(AssociatedStats::of).toList());
    }

    @Override
    public List<AssociatedStats> associatedStats(String uid, Long fieldId, String collectTime, String grade) {
        // 1. 查源点位获取关联维度值
        RealtimeCollectData source = collectDataService.getSourcePoint(uid, fieldId, collectTime);
        if(source == null) return Collections.emptyList();

        // 2. 分两次查询，按(uid, field_id)去重合并
        if(source.getCid() == null && source.getPuid() == null && source.getDeviceGroupId() == null) {
            return Collections.emptyList();
        }

        Map<String, AssociatedStats> map = new LinkedHashMap<>();

        // 2.1 同一采集器 — 精确时间匹配
        if(source.getCid() != null) {
            List<RealtimeCollectData> list = collectDataService.listByCid(
                    source.getCid(), uid, fieldId, grade);
            mergeAssociated(map, list);
        }

        // 2.2 同一网关 / 同一设备分组 — ±60秒范围
        if(source.getPuid() != null || source.getDeviceGroupId() != null) {
            Date startTime = DateUtil.offsetSecond(DateUtil.parse(collectTime), -60);
            Date endTime = DateUtil.offsetSecond(DateUtil.parse(collectTime), 60);
            List<RealtimeCollectData> list = collectDataService.listByPuidOrGroup(
                    source.getPuid(), source.getDeviceGroupId(), startTime, endTime, uid, fieldId, grade);
            mergeAssociated(map, list);
        }

        // 3. 补充设备分组名称
        List<AssociatedStats> result = new ArrayList<>(map.values());
        return resolverDeviceGroupName(result);
    }

    /**
     * 合并关联查询结果到map，跳过源点位，标记关联维度
     */
    private void mergeAssociated(Map<String, AssociatedStats> map, List<RealtimeCollectData> list) {
        for(RealtimeCollectData d : list) {
            String key = d.getUid() + ":" + d.getField();
            if(!map.containsKey(key)) {
                map.put(key, AssociatedStats.of(d));
            }
        }
    }

    private List<AssociatedStats> resolverDeviceGroupName(List<AssociatedStats> list) {
        if(CollectionUtil.isNotEmpty(list)) {
            list.forEach(item -> {
                if(item.getDeviceGroupId() != null) {
                    DeviceGroup cacheById = deviceGroupService.getCacheById(item.getDeviceGroupId());
                    if(cacheById != null) {
                        item.setDeviceGroupName(cacheById.getName());
                    }
                }
            });
        }

        return list;
    }
}
