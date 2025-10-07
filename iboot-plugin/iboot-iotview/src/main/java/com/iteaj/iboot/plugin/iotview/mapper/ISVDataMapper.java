package com.iteaj.iboot.plugin.iotview.mapper;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.spi.iot.view.DataItemOption;
import com.iteaj.framework.spi.iot.view.global.GlobalApiParams;
import com.iteaj.iboot.plugin.iotview.dto.DeviceStatusCountDto;

import java.util.List;
import java.util.Map;

public interface ISVDataMapper extends Mapper {

    List<DataItemOption> listProductOption(boolean hasGateway);

    List<DataItemOption> listModelAttrOption(String productCode);

    List<DataItemOption> listGroupOption();

    List<DeviceStatusCountDto> countDeviceStatus(String status, String range, List<String> values);

    List<DataItemOption> listDeviceOptions(String productCode);

    List<DeviceStatusCountDto> countDeviceStatusByRange(String status, String range, List<String> values);

    GlobalApiParams countAggRealtimeOfGlobal(GlobalApiParams params);

    GlobalApiParams countDeviceStatusInfoOfGlobal(GlobalApiParams params);

    GlobalApiParams countDeviceWarnOfGlobal(GlobalApiParams req);

    GlobalApiParams countBasicProductInfoOfGlobal(GlobalApiParams req);

    GlobalApiParams countBasicDriverInfoOfGlobal(GlobalApiParams req);

    IPage<Map<String, String>> listBasicProduct(Page<?> page, Map<String, Object> params);
}
