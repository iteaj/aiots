package com.iteaj.framework.spi.iot.view;

import org.springframework.core.Ordered;

import java.util.List;

public interface ViewApi extends Ordered {

    int ORDER = 8000;

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

    ViewApiType getApiType();

    /**
     * 初始化
     */
    default ViewApi init() {
        return this;
    }

    @Override
    default int getOrder() {
        return ORDER;
    }

    /**
     * 数据列表
     * @return
     */
    List<DataItemOption> basicOptions();
}
