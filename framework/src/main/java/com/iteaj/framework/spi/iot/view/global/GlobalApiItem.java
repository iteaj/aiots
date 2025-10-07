package com.iteaj.framework.spi.iot.view.global;

import com.iteaj.framework.spi.iot.view.ApiConfigOption;
import com.iteaj.framework.spi.iot.view.DataItemOption;
import com.iteaj.framework.spi.iot.view.ViewApi;
import com.iteaj.framework.spi.iot.view.ViewApiType;

import java.util.List;

public interface GlobalApiItem extends ViewApi {

    @Override
    default ViewApiType getApiType() {
        return ViewApiType.GLOBAL_API;
    }

    /**
     * 接口请求
     * @param params 请求参数
     * @return api响应
     */
    GlobalApiParams fetch(GlobalApiParams params);

    /**
     * 此option必须包含 {@link ApiConfigOption}
     * 而且此配置必须放在树形结构的倒数第二级
     * @return 解析信息
     */
    @Override
    List<DataItemOption> basicOptions();
}
