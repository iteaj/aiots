package com.iteaj.framework.spi.iot.view.table;

import com.iteaj.framework.spi.iot.view.ViewApi;
import com.iteaj.framework.spi.iot.view.ViewApiType;

/**
 * 表格接口项
 */
public interface TableApiItem extends ViewApi {

    @Override
    default ViewApiType getApiType() {
        return ViewApiType.TABLE_API;
    }

    /**
     * 接口请求
     * @param params 请求参数
     * @return api响应结果
     */
    TableApiResult fetch(TableApiParams params);
}
