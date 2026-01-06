package com.iteaj.framework.spi.iot.view.chart;

import com.iteaj.framework.spi.iot.view.DataItemOption;
import com.iteaj.framework.spi.iot.view.ViewApi;
import com.iteaj.framework.spi.iot.view.ViewApiType;

import java.util.List;


/**
 * 图表的api接口
 */
public interface ChartApiItem extends ViewApi {

    /**
     * key
     * @return
     */
    String getKey();

    /**
     * 名称
     * @return
     */
    String getName();

    @Override
    default ViewApiType getApiType() {
        return ViewApiType.CHART_API;
    }

    List<ChartFetchResult> fetch(List<ChartApiParams.Value> values, ChartApiParams params);

    /**
     * 动态加载option
     * @return
     */
    List<DataItemOption> loadOptions(String[] args);
}
