package com.iteaj.framework.spi.iot.view;

/**
 * 表格专用接口
 */
public interface ViewTableApiItem extends GlobalApiItem{

    /**
     * 接口请求
     * @param params 请求参数
     * @return api响应
     */
    GlobalApiParams fetch(GlobalApiParams params);
}
