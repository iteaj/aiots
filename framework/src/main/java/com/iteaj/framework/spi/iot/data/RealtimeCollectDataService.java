package com.iteaj.framework.spi.iot.data;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.spi.iot.consts.TimeCountCondition;

import java.util.List;

public interface RealtimeCollectDataService {

    PageResult<Page> detailOfPage(Page page, RealtimeCollectData entity);

    /**
     * 通过条件统计
     * @param condition
     * @return
     */
    List<BaseTimeCount> countByCondition(TimeCountCondition condition);

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
