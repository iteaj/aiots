package com.iteaj.iboot.module.iot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.spi.iot.data.IndicatorStats;
import com.iteaj.framework.spi.iot.data.RealtimeCollectData;
import com.iteaj.framework.spi.iot.data.RealtimeCollectDataService;
import com.iteaj.framework.spi.iot.data.RealtimeDataParam;
import com.iteaj.iboot.module.iot.dto.AssociatedStats;
import com.iteaj.iboot.module.iot.dto.IndicatorCardVO;
import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.iteaj.iboot.module.iot.entity.Product;
import com.iteaj.iboot.module.iot.service.IDataIndicatorService;
import com.iteaj.iboot.module.iot.service.IDeviceGroupService;
import com.iteaj.iboot.module.iot.service.IProductService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 数据指标服务实现
 */
@Service
public class DataIndicatorServiceImpl implements IDataIndicatorService {

    private final RealtimeCollectDataService collectDataService;
    private final IDeviceGroupService deviceGroupService;
    private final IProductService productService;

    public DataIndicatorServiceImpl(RealtimeCollectDataService collectDataService, IDeviceGroupService deviceGroupService, IProductService productService) {
        this.collectDataService = collectDataService;
        this.deviceGroupService = deviceGroupService;
        this.productService = productService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Page<IndicatorCardVO> viewOfPage(Page page, RealtimeDataParam param) {
        // 固定条件: 只查采集成功且有告警等级的数据
        param.setStatus(true);
        param.setHasGrade(true);

        // 产品id转产品码(采集表按product_code过滤)
        if(param.getProductId() != null && StrUtil.isEmpty(param.getProductCode())) {
            Product product = productService.getById(param.getProductId())
                    .ifNotPresentThrow("产品不存在")
                    .getData();
            param.setProductCode(product.getCode());
        }

        // 复用采集数据分页查询, 再裁剪为前端卡片需要的字段
        collectDataService.listIndicatorOfPage(page, param);
        List<IndicatorCardVO> records = ((List<RealtimeCollectData>) page.getRecords())
                .stream().map(IndicatorCardVO::of).toList();

        if(!records.isEmpty()) {
            records.forEach(item -> {
                DeviceGroup cacheById = deviceGroupService.getCacheById(item.getGroupId());
                if(cacheById != null) {
                    item.setDeviceGroupName(cacheById.getName());
                }
            });
        }

        Page<IndicatorCardVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(records);
        return result;
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
    public List<AssociatedStats> crossDevicePoints(Long fieldId, String collectTime, String excludeUid) {
        List<RealtimeCollectData> collectData = collectDataService.crossDevicePoints(null, fieldId, collectTime, excludeUid);
        return resolverDeviceGroupName(collectData.stream().map(AssociatedStats::of).toList());
    }

    @Override
    public List<AssociatedStats> associatedStats(String uid, Long fieldId, String collectTime, String grade, Integer offsetSeconds) {
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

        // 2.2 同一网关 / 同一设备分组 — ±offsetSeconds范围(默认60秒)
        if(source.getPuid() != null || source.getDeviceGroupId() != null) {
            int offset = offsetSeconds != null && offsetSeconds > 0 ? offsetSeconds : 60;
            Date startTime = DateUtil.offsetSecond(DateUtil.parse(collectTime), -offset);
            Date endTime = DateUtil.offsetSecond(DateUtil.parse(collectTime), offset);
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
