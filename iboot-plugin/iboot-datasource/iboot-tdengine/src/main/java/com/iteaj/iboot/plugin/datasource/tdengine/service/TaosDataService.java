package com.iteaj.iboot.plugin.datasource.tdengine.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.spi.iot.consts.TimeCountCondition;
import com.iteaj.framework.spi.iot.data.*;

import java.util.List;

public class TaosDataService implements RealtimeCollectDataService {

    @Override
    public PageResult detailOfPage(Page page, RealtimeCollectData entity) {
        return null;
    }

    @Override
    public List<EchartsCountValue> countOfCommonByTime(TimeCountCondition condition) {
        return null;
    }

    @Override
    public List<BaseEchartsCount> countLastMonth() {
        return null;
    }

    @Override
    public List<BaseEchartsCount> countLastWeek(Long deviceGroupId) {
        return null;
    }

    @Override
    public List<CollectValueItem> listOfCommonByTime(CollectDataCondition condition) {
        return null;
    }
}
