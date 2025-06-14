package com.iteaj.framework.spi.iot.view;

import org.springframework.core.Ordered;

import java.util.List;

public interface ViewApiItem extends Ordered {

    int ORDER = 8000;

    /**
     * key
     * @return
     */
    String getKey();

    /**
     * 初始化
     */
    default ViewApiItem init() {
        return this;
    }

    /**
     * 名称
     * @return
     */
    String getName();

    @Override
    default int getOrder() {
        return ORDER;
    }

    List<ViewFetchResult> fetch(List<ViewApiParams.Value> values, ViewApiParams params);

    /**
     * 数据列表
     * @return
     */
    List<DataItemOption> basicOptions();

    /**
     * 动态加载option
     * @return
     */
    List<DataItemOption> loadOptions(String[] args);
}
