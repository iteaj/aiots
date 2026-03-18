package com.iteaj.iboot.plugin.ai.mcp.mapper;

import com.iteaj.iboot.plugin.ai.mcp.dto.DeviceDriverDto;
import com.iteaj.iboot.plugin.ai.mcp.dto.DeviceGroupInfoDto;
import com.iteaj.iboot.plugin.ai.mcp.entity.ProductInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 物联网平台相关数据查询Mapper
 */
@Mapper
public interface IotInfoMapper {

    ProductInfo getProductInfo(String productCode);

    List<DeviceGroupInfoDto> listDeviceGroupInfo(Long deviceGroupId, String name);

    List<DeviceDriverDto> listDeviceProtocol();
}
