package com.iteaj.framework.spi.iot.view.global;

import com.iteaj.framework.spi.iot.view.ApiConfigOption;
import com.iteaj.framework.spi.iot.view.DataItemOption;
import com.iteaj.framework.spi.iot.view.ViewApi;
import com.iteaj.framework.spi.iot.view.ViewApiType;

import java.util.List;

/**
 * 全局功能接口
 */
public interface GlobalFuncApi extends ViewApi {

    @Override
    default ViewApiType getApiType() {
        return ViewApiType.GLOBAL_FUNC_API;
    }

    /**
     * 功能接口调用
     * @param params 调用参数
     */
    void invoke(GlobalFuncApiParams params);

    /**
     * 此option必须包含 {@link ApiConfigOption}
     * 而且此配置必须放在树形结构的倒数第二级
     * @return 解析信息
     */
    @Override
    List<DataItemOption> basicOptions();
}
