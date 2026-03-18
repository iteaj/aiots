package com.iteaj.iboot.plugin.ai.mcp.service.impl;

import com.iteaj.iboot.plugin.ai.mcp.dto.DeviceDriverDto;
import com.iteaj.iboot.plugin.ai.mcp.dto.DeviceGroupInfoDto;
import com.iteaj.iboot.plugin.ai.mcp.entity.ProductInfo;
import com.iteaj.iboot.plugin.ai.mcp.mapper.IotInfoMapper;
import com.iteaj.iboot.plugin.ai.mcp.service.IotInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IotInfoServiceImpl implements IotInfoService {

    private final IotInfoMapper iotInfoMapper;

    public IotInfoServiceImpl(IotInfoMapper iotInfoMapper) {
        this.iotInfoMapper = iotInfoMapper;
    }


    @Override
    public ProductInfo getProductInfo(String productCode) {
        return iotInfoMapper.getProductInfo(productCode);
    }

    @Override
    public List<DeviceGroupInfoDto> listDeviceGroupInfo(Long deviceGroupId, String name) {
        return iotInfoMapper.listDeviceGroupInfo(deviceGroupId, name);
    }

    @Override
    public List<DeviceDriverDto> listDeviceProtocol() {
        return iotInfoMapper.listDeviceProtocol();
    }
}
