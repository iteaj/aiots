package com.iteaj.framework.spi.iot.data;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.spi.iot.consts.TimeCountCondition;

import java.util.List;

public interface RealtimeCollectDataService {

    PageResult detailOfPage(Page page, RealtimeCollectData entity);

    /**
     * 通过条件统计
     * @param condition
     * @return
     */
    List<BaseTimeCount> countByTime(TimeCountCondition condition);

    default void validateCondition(TimeCountCondition condition) {
        if(condition.getTimeType() == null) {
            throw new ServiceException("时间类型未指定[timeType]");
        }

        if(CollectionUtil.isEmpty(condition.getValue())) {
            throw new ServiceException("未指定类型值[value]");
        }

        if(CollectionUtil.isEmpty(condition.getTimeRange())) {
            throw new ServiceException("未指定时间范围[timeRange]");
        }
    }

    /**
     * 统计最近一个月的数据
     * @return
     */
    List<BaseTimeCount> countLastMonth();

    /**
     * 统计最近一周的数据
     * @param deviceGroupId
     * @return
     */
    List<BaseTimeCount> countLastWeek(Long deviceGroupId);
}
