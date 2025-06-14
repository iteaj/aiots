package com.iteaj.iboot.plugin.iotview.service;

import com.iteaj.framework.spi.iot.view.DataItemOption;
import com.iteaj.iboot.plugin.iotview.dto.DeviceStatusCountDto;

import java.util.List;

public interface ISVDataService {

    List<DataItemOption> listProductOption(boolean isGateway);

    List<DataItemOption> listModelAttrOption(String productId);

    List<DataItemOption> listGroupOption();

    List<DataItemOption> listDeviceOptions(String productCode);

    DeviceStatusCountDto countDeviceStatus(String status, String range, String rangeValue);
}
