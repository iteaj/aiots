package com.iteaj.iboot.module.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.spi.iot.consts.TimeCountCondition;
import com.iteaj.framework.spi.iot.data.BaseEchartsCount;
import com.iteaj.framework.spi.iot.data.CollectDataCondition;
import com.iteaj.framework.spi.iot.data.RealtimeCollectData;
import com.iteaj.framework.spi.iot.view.chart.ChartSqlCondition;
import com.iteaj.iboot.module.iot.dto.CollectDataDto;
import com.iteaj.iboot.module.iot.dto.IndicatorStats;
import com.iteaj.iboot.module.iot.entity.CollectData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CollectDataMapper extends BaseMapper<CollectData> {

    Page<CollectDataDto> detailOfPage(Page page, RealtimeCollectData entity);

    /**
     * 统计最近一周的数据
     * @param deviceGroupId
     * @return
     */
    List<BaseEchartsCount> countLastWeek(Long deviceGroupId);

    /**
     * 统计最近指定天的数据
     * @param days 天数
     * @return
     */
    List<BaseEchartsCount> countLastByDays(int days);

    /**
     * 通用的时间统计
     * @return
     * @param item
     */
    List<BaseEchartsCount> countOfCommonByTime(TimeCountCondition.TimeCountSqlCondition item);

    /**
     * 通用的时间列表
     * @param item
     * @return
     */
    List<CollectDataDto> listOfCommonByTime(CollectDataCondition.DataSqlCondition item);

    Page pageOfCommonByTime(Page<CollectDataDto> page, CollectDataCondition.DataSqlCondition condition);

    Page<CollectDataDto> productOfPage(Page page, RealtimeCollectData entity);

    List<BaseEchartsCount> countOfViewByTime(ChartSqlCondition condition);

    List<BaseEchartsCount> countOfViewByRange(ChartSqlCondition condition);

    /**
     * 获取点位聚合统计（max/min/avg/count + 极值发生时间）
     */
    IndicatorStats statsOfField(@Param("uid") String uid, @Param("fieldId") Long fieldId,
                                @Param("start") String start, @Param("end") String end);

    /**
     * 获取点位在时间范围内的采集数据列表
     */
    List<CollectData> listOfData(@Param("uid") String uid, @Param("fieldId") Long fieldId,
                                 @Param("start") String start, @Param("end") String end);

    /**
     * 获取设备其他点位在同一采集时间的数据
     */
    List<CollectData> associatedPoints(@Param("uid") String uid, @Param("excludeFieldId") Long excludeFieldId,
                                       @Param("collectTime") String collectTime);

    /**
     * 获取同产品下其他设备同点位数据
     */
    List<CollectData> crossDevicePoints(@Param("productCode") String productCode, @Param("fieldId") Long fieldId,
                                        @Param("collectTime") String collectTime, @Param("excludeUid") String excludeUid);
}
