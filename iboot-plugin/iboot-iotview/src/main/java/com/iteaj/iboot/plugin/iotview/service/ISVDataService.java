package com.iteaj.iboot.plugin.iotview.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.spi.iot.view.DataItemOption;
import com.iteaj.framework.spi.iot.view.global.GlobalApiParams;
import com.iteaj.iboot.plugin.iotview.dto.DeviceStatusCountDto;

import java.util.List;
import java.util.Map;

public interface ISVDataService {

    List<DataItemOption> listProductOption(boolean isGateway);

    List<DataItemOption> listModelAttrOption(String productCode);

    List<DataItemOption> listGroupOption();

    List<DataItemOption> listDeviceOptions(String productCode);

    List<DeviceStatusCountDto> countDeviceStatus(String status, String category, String range, List<String> values);

    /**
     * 统计全局接口的采集的聚合数据
     * @param params
     * @return
     */
    GlobalApiParams countAggRealtimeOfGlobal(GlobalApiParams params);

    /**
     * 统计全局接口的设备状态信息
     * @param params
     * @return
     */
    GlobalApiParams countDeviceStatusInfoOfGlobal(GlobalApiParams params);

    /**
     * 统计全局接口的告警状态信息
     * @param req
     * @return
     */
    GlobalApiParams countDeviceWarnOfGlobal(GlobalApiParams req);

    GlobalApiParams countBasicProductInfoOfGlobal(GlobalApiParams req);

    GlobalApiParams countBasicDriverInfoOfGlobal(GlobalApiParams req);

    void listBasicProduct(Page<?> page, Map<String, Object> params);
}
