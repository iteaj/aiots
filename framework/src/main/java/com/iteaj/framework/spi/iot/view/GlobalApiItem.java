package com.iteaj.framework.spi.iot.view;

import java.util.List;

public interface GlobalApiItem extends ViewApi{

    /**
     * 接口请求
     * @param params 请求参数
     * @return api响应
     */
    GlobalApiParams fetch(GlobalApiParams params);

    /**
     * 此option必须包含 {@link GlobalApiConfig}
     * 而且此配置必须放在树形结构的倒数第二级
     * @return 解析信息
     */
    @Override
    List<DataItemOption> basicOptions();
}
