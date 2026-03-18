package com.iteaj.iboot.plugin.ai.mcp.service;

import com.iteaj.iboot.plugin.ai.mcp.dto.DeviceDriverDto;
import com.iteaj.iboot.plugin.ai.mcp.dto.DeviceGroupInfoDto;
import com.iteaj.iboot.plugin.ai.mcp.entity.ProductInfo;

import java.util.List;

public interface IotInfoService {

    ProductInfo getProductInfo(String productCode);

    List<DeviceGroupInfoDto> listDeviceGroupInfo(Long deviceGroupId, String name);

    List<DeviceDriverDto> listDeviceProtocol();
}
