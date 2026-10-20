package com.iteaj.iboot.plugin.datasource.tdengine.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.spi.iot.consts.CollectSearchType;
import com.iteaj.framework.spi.iot.consts.TimeCountCondition;
import com.iteaj.framework.spi.iot.data.*;
import com.iteaj.framework.spi.iot.view.chart.ChartSqlCondition;
import com.iteaj.iboot.plugin.datasource.tdengine.mapper.TaosCollectDataMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaosDataService implements RealtimeCollectDataService {

    @Autowired
    private TaosCollectDataMapper collectDataMapper;

    @Override
    public PageResult detailOfPage(Page page, RealtimeCollectData entity) {
        try {
        return new PageResult<>(collectDataMapper.detailOfPage(page, entity));
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if(cause instanceof SQLException && cause.getMessage().contains("0x80002603")) {
                throw new ServiceException("设备("+entity.getUid()+")表不存在", e);
            }

            throw new ServiceException("查询数据失败", e);
        }
    }

    @Override
    public List<EchartsCountValue> countOfCommonByTime(TimeCountCondition condition) {
        List<EchartsCountValue> values = new ArrayList<>();
        condition.buildSqlConditions().forEach(item -> {
            try {
                List<BaseEchartsCount> counts = collectDataMapper.countOfCommonByTime(item);
                List<EchartsCountValue> countValues = condition.buildResult(item, counts);
                values.addAll(countValues);
            } catch (Exception e) {
                Throwable cause = e.getCause();
                if(cause instanceof SQLException && cause.getMessage().contains("0x80002603")) {
                    throw new ServiceException("设备("+item.getUid()+")表不存在", e);
                }

                throw new ServiceException("查询数据失败", e);
            }
        });

        return values;
    }

    @Override
    public List<BaseEchartsCount> countLastMonth() {
        Map<String, BaseEchartsCount> map = new HashMap<>();
        List<BaseEchartsCount> dtos = collectDataMapper.countLastByDays(29);
        if(CollectionUtil.isNotEmpty(dtos)) {
            map = dtos.stream().collect(Collectors.toMap(BaseEchartsCount::getCategory, item -> item));
        }

        List<BaseEchartsCount> result = new ArrayList<>();
        DateTime lastWeek = new DateTime().offset(DateField.DAY_OF_MONTH, -29);
        for (int i = 0; i < 30; i++) {
            String time = lastWeek.toString("yyyy-MM-dd");
            BaseEchartsCount lastWeekDto = map.get(time);
            if(lastWeekDto != null) {
                result.add(lastWeekDto);
            } else {
                result.add(BaseEchartsCount.buildZero(time));
            }

            lastWeek = lastWeek.offset(DateField.DAY_OF_MONTH, 1);
        }

        return result;
    }

    @Override
    public List<BaseEchartsCount> countLastWeek(Long deviceGroupId) {
        Map<String, BaseEchartsCount> map = new HashMap<>();
        List<BaseEchartsCount> dtos = collectDataMapper.countLastWeek(deviceGroupId);
        if(CollectionUtil.isNotEmpty(dtos)) {
            map = dtos.stream().collect(Collectors.toMap(BaseEchartsCount::getCategory, item -> item));
        }

        List<BaseEchartsCount> result = new ArrayList<>();
        DateTime lastWeek = new DateTime().offset(DateField.DAY_OF_MONTH, -7);
        for (int i = 0; i < 7; i++) {
            String time = lastWeek.toString("yyyy-MM-dd");
            BaseEchartsCount lastWeekDto = map.get(time);
            if(lastWeekDto != null) {
                result.add(lastWeekDto);
            } else {
                result.add(new BaseEchartsCount().buildZero(time));
            }

            lastWeek = lastWeek.offset(DateField.DAY_OF_MONTH, 1);
        }

        return result;
    }

    @Override
    public List<CollectValueItem> listOfCommonByTime(CollectDataCondition condition) {
        List<CollectValueItem> values = new ArrayList<>();
        if(condition.getSearchType() == CollectSearchType.list) {
            condition.buildSqlConditions().forEach(item -> {
                try {
                List<RealtimeCollectData> counts = collectDataMapper.listOfCommonByTime(item);
                List<CollectValueItem> valueItems = condition.buildResult(item, counts, 0);
                values.addAll(valueItems);
                } catch (Exception e) {
                    Throwable cause = e.getCause();
                    if(cause instanceof SQLException && cause.getMessage().contains("0x80002603")) {
                        throw new ServiceException("设备("+item.getUid()+")表不存在", e);
                    }

                    throw new ServiceException("查询数据失败", e);
                }
            });
        } else {
            condition.buildSqlConditions().forEach(item -> {
                if(item.getFields().size() > 1) {
                    throw new ServiceException("分页搜索不支持同时多个字段");
                }

                try {
                    Page<RealtimeCollectData> page = new Page<>(condition.getCurrent(), condition.getSize());
                    collectDataMapper.pageOfCommonByTime(page, item);
                    List<CollectValueItem> valueItems = condition.buildResult(item, page.getRecords(), page.getTotal());
                    values.addAll(valueItems);
                } catch (Exception e) {
                    Throwable cause = e.getCause();
                    if(cause instanceof SQLException && cause.getMessage().contains("0x80002603")) {
                        throw new ServiceException("设备("+item.getUid()+")表不存在", e);
                    }

                    throw new ServiceException("查询数据失败", e);
                }
            });
        }

        return values;
    }

    @Override
    public PageResult productOfPage(Page page, RealtimeCollectData entity) {
        Page<RealtimeCollectData> product = collectDataMapper.productOfPage(page, entity);
        if(CollectionUtil.isNotEmpty(product.getRecords())) {
            List<DefaultRealtimeData> datas = collectDataMapper.listProductData(page.getRecords());
            Map<String, List<DefaultRealtimeData>> collect = datas.stream().collect(Collectors.groupingBy(item -> item.getCid() + item.getUid()));
            List<RealtimeCollectData> collected = collect.values().stream().map((value) -> {
                DefaultRealtimeData realtimeData = value.get(0);
                String joinValue = value.stream()
                        .map(item -> item.getField() + "=" + (item.getStatus() ? item.getValue() : item.getReason()))
                        .collect(Collectors.joining(":::"));
                return realtimeData.setValue(joinValue);
            }).sorted((a, b) -> Math.toIntExact(b.getCollectTime().getTime() - a.getCollectTime().getTime()))
                    .collect(Collectors.toList());

            product.setRecords(collected);
        }

        return new PageResult(product);
    }

    @Override
    public List<BaseEchartsCount> countOfView(ChartSqlCondition condition) {
        return null;
    }

    @Override
    public IndicatorStats statsOfField(String uid, Long fieldId, String start, String end) {
        try {
            return collectDataMapper.statsOfField(uid, fieldId, start, end);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if(cause instanceof SQLException && cause.getMessage().contains("0x80002603")) {
                throw new ServiceException("设备("+uid+")表不存在", e);
            }

            throw new ServiceException("查询数据失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RealtimeCollectData> listOfData(String uid, Long fieldId, String start, String end) {
        try {
            return (List<RealtimeCollectData>)(List<?>) collectDataMapper.listOfData(uid, fieldId, start, end);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if(cause instanceof SQLException && cause.getMessage().contains("0x80002603")) {
                throw new ServiceException("设备("+uid+")表不存在", e);
            }

            throw new ServiceException("查询数据失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RealtimeCollectData> associatedPoints(String uid, Long excludeFieldId, String collectTime) {
        try {
        return (List<RealtimeCollectData>)(List<?>) collectDataMapper.associatedPoints(uid, excludeFieldId, collectTime);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if(cause instanceof SQLException && cause.getMessage().contains("0x80002603")) {
                throw new ServiceException("设备("+uid+")表不存在", e);
            }

            throw new ServiceException("查询数据失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RealtimeCollectData> crossDevicePoints(String productCode, Long fieldId, String collectTime, String excludeUid) {
        return (List<RealtimeCollectData>)(List<?>) collectDataMapper.crossDevicePoints(productCode, fieldId, collectTime, excludeUid);
    }

    @Override
    public RealtimeCollectData getSourcePoint(String uid, Long fieldId, String collectTime) {
        return collectDataMapper.getSourcePoint(uid, fieldId, collectTime);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RealtimeCollectData> listByCid(String cid, String excludeUid, Long excludeFieldId, String grade) {
        return (List<RealtimeCollectData>)(List<?>) collectDataMapper.listByCid(cid, excludeUid, excludeFieldId, grade);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RealtimeCollectData> listByPuidOrGroup(String puid, Long deviceGroupId, Date startTime, Date endTime, String excludeUid, Long excludeFieldId, String grade) {
        return (List<RealtimeCollectData>)(List<?>) collectDataMapper.listByPuidOrGroup(puid, deviceGroupId, startTime, endTime, excludeUid, excludeFieldId, grade);
    }

    @Override
    public Page<?> listIndicatorOfPage(Page<?> page, RealtimeDataParam param) {
        try {
            return collectDataMapper.listIndicatorOfPage(page, param);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if(cause instanceof SQLException && cause.getMessage().contains("0x80002603")) {
                throw new ServiceException("设备("+param.getUid()+")表不存在", e);
            }

            throw new ServiceException("查询指标数据失败", e);
        }
    }
}
