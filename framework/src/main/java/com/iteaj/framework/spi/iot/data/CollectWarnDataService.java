package com.iteaj.framework.spi.iot.data;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.spi.iot.consts.TimeCountCondition;

import java.util.List;

public interface CollectWarnDataService {

    PageResult<?> detailPage(Page page, WarnData entity);

    BooleanResult batchRemove(List ids);

    /**
     * 通过条件统计
     * @param condition
     * @return
     */
    List<EchartsCountValue> countOfCommonByTime(TimeCountCondition condition);

    /**
     * 通过时间条件获取数据
     * @param condition
     * @return
     */
    List<CollectValueItem> listOfCommonByTime(CollectDataCondition condition);
}
