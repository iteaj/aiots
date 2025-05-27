package com.iteaj.iboot.plugin.datasource.tdengine.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.spi.iot.consts.TimeCountCondition;
import com.iteaj.framework.spi.iot.data.BaseEchartsCount;
import com.iteaj.framework.spi.iot.data.CollectDataCondition;
import com.iteaj.framework.spi.iot.data.RealtimeCollectData;

import java.util.List;

import static com.iteaj.iboot.plugin.datasource.tdengine.mapper.TaosCollectDataMapper.DS_NAME;

@DS(DS_NAME)
public interface TaosCollectDataMapper {

    String DS_NAME = "tdengine";

    Page<RealtimeCollectData> detailOfPage(Page page, RealtimeCollectData entity);

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
    List<RealtimeCollectData> listOfCommonByTime(CollectDataCondition.DataSqlCondition item);

    Page pageOfCommonByTime(Page<RealtimeCollectData> page, CollectDataCondition.DataSqlCondition condition);

    Page<RealtimeCollectData> productOfPage(Page page, RealtimeCollectData entity);
}
