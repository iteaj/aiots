package com.iteaj.iboot.module.iot.service;

import com.iteaj.iboot.module.iot.dto.IndicatorStats;
import com.iteaj.iboot.module.iot.entity.CollectData;

import java.util.List;

/**
 * 数据指标服务
 */
public interface IDataIndicatorService {

    /**
     * 获取点位在指定时间范围内的聚合统计
     * @param uid     设备标识
     * @param fieldId 属性ID
     * @param start   开始时间
     * @param end     结束时间
     * @return 聚合统计（max/maxTime/min/minTime/avg/count）
     */
    IndicatorStats getStats(String uid, Long fieldId, String start, String end);

    /**
     * 获取点位在指定时间范围内的采集数据列表
     * @param uid     设备标识
     * @param fieldId 属性ID
     * @param start   开始时间
     * @param end     结束时间
     * @return 采集数据列表
     */
    List<CollectData> listData(String uid, Long fieldId, String start, String end);

    /**
     * 获取设备其他点位在指定采集时间的关联数据
     * @param uid            设备标识
     * @param excludeFieldId 排除的属性ID
     * @param collectTime    采集时间（精确到秒）
     * @return 同设备其他点位采集数据
     */
    List<CollectData> associatedPoints(String uid, Long excludeFieldId, String collectTime);

    /**
     * 获取同产品下其他设备同点位在指定采集时间的数据
     * @param productCode 产品编码
     * @param fieldId     属性ID
     * @param collectTime 采集时间（精确到秒）
     * @param excludeUid  排除的设备标识
     * @return 其他设备的同点位数据
     */
    List<CollectData> crossDevicePoints(String productCode, Long fieldId, String collectTime, String excludeUid);
}
