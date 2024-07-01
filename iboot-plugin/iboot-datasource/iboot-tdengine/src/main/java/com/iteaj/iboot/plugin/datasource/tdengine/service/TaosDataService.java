package com.iteaj.iboot.plugin.datasource.tdengine.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.spi.iot.consts.TimeCountCondition;
import com.iteaj.framework.spi.iot.data.BaseTimeCount;
import com.iteaj.framework.spi.iot.data.RealtimeCollectData;
import com.iteaj.framework.spi.iot.data.RealtimeCollectDataService;

import java.util.List;

public class TaosDataService implements RealtimeCollectDataService {

    @Override
    public PageResult detailOfPage(Page page, RealtimeCollectData entity) {
        return null;
    }

    @Override
    public List<BaseTimeCount> countByTime(TimeCountCondition condition) {
        return null;
    }

    @Override
    public List<BaseTimeCount> countLastMonth() {
        return null;
    }

    @Override
    public List<BaseTimeCount> countLastWeek(Long deviceGroupId) {
        return null;
    }
}
