package com.iteaj.iboot.plugin.iotview.mapper;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.iteaj.framework.spi.iot.view.DataItemOption;
import com.iteaj.iboot.plugin.iotview.dto.DeviceStatusCountDto;

import java.util.List;

public interface ISVDataMapper extends Mapper {

    List<DataItemOption> listProductOption(boolean hasGateway);

    List<DataItemOption> listModelAttrOption(String productCode);

    List<DataItemOption> listGroupOption();

    List<DeviceStatusCountDto> countDeviceStatus(String status, String range, List<String> values);

    List<DataItemOption> listDeviceOptions(String productCode);

    List<DeviceStatusCountDto> countDeviceStatusByTime(String status, String range, List<String> values);

    List<DeviceStatusCountDto> countDeviceStatusByRange(String status, String range, List<String> values);
}
