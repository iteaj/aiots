package com.iteaj.framework.spi.iot.view;

import org.springframework.core.Ordered;

import java.util.List;

public interface ViewApiItem extends ViewApi {


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

    List<ViewFetchResult> fetch(List<ViewApiParams.Value> values, ViewApiParams params);

    /**
     * 动态加载option
     * @return
     */
    List<DataItemOption> loadOptions(String[] args);
}
