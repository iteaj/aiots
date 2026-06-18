package com.iteaj.framework.spi.iot.data;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.spi.iot.consts.TimeCountCondition;
import com.iteaj.framework.spi.iot.view.chart.ChartSqlCondition;

import java.util.*;

public interface RealtimeCollectDataService {

    PageResult detailOfPage(Page page, RealtimeCollectData entity);

    /**
     * 通过条件统计
     * @param condition
     * @return
     */
    List<EchartsCountValue> countOfCommonByTime(TimeCountCondition condition);

    /**
     * 统计最近一个月的数据
     * @return
     */
    List<BaseEchartsCount> countLastMonth();

    /**
     * 统计最近一周的数据
     * @param deviceGroupId
     * @return
     */
    List<BaseEchartsCount> countLastWeek(Long deviceGroupId);

    /**
     * 根据条件获取实时值
     * @see CollectDataCondition#getTimeType()
     * @param condition
     * @return
     */
    List<CollectValueItem> listOfCommonByTime(CollectDataCondition condition);

    /**
     * 根据产品模型获取数据
     * @param page
     * @param entity
     * @return
     */
    PageResult productOfPage(Page page, RealtimeCollectData entity);

    List<BaseEchartsCount> countOfView(ChartSqlCondition condition);

    /**
     * 获取点位在指定时间范围内的聚合统计
     */
    IndicatorStats statsOfField(String uid, Long fieldId, String start, String end);

    /**
     * 获取点位在时间范围内的采集数据列表
     */
    List<RealtimeCollectData> listOfData(String uid, Long fieldId, String start, String end);

    /**
     * 获取设备其他点位在同一采集时间的数据
     */
    List<RealtimeCollectData> associatedPoints(String uid, Long excludeFieldId, String collectTime);

    /**
     * 获取同产品下其他设备同点位数据
     */
    List<RealtimeCollectData> crossDevicePoints(String productCode, Long fieldId, String collectTime, String excludeUid);

    /**
     * 查源点位信息（用于关联查询）
     */
    RealtimeCollectData getSourcePoint(String uid, Long fieldId, String collectTime);

    /**
     * 按采集器(cid)查询同一采集批次的其他点位
     */
    List<RealtimeCollectData> listByCid(String cid, String excludeUid, Long excludeFieldId, String grade);

    /**
     * 按网关(puid)或设备组查询关联点位
     */
    List<RealtimeCollectData> listByPuidOrGroup(String puid, Long deviceGroupId, Date startTime, Date endTime, String excludeUid, Long excludeFieldId, String grade);
}
