package com.iteaj.iboot.plugin.iotview.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.spi.iot.view.DataItemOption;
import com.iteaj.framework.spi.iot.view.global.GlobalApiParams;
import com.iteaj.iboot.plugin.iotview.dto.DeviceStatusCountDto;
import com.iteaj.iboot.plugin.iotview.mapper.ISVDataMapper;
import com.iteaj.iboot.plugin.iotview.service.ISVDataService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ISVDataServiceImpl implements ISVDataService {

    private final ISVDataMapper isvDataMapper;

    public ISVDataServiceImpl(ISVDataMapper isvDataMapper) {
        this.isvDataMapper = isvDataMapper;
    }

    @Override
    public List<DataItemOption> listProductOption(boolean isGateway) {
        return isvDataMapper.listProductOption(isGateway);
    }

    @Override
    public List<DataItemOption> listModelAttrOption(String productCode) {
        return isvDataMapper.listModelAttrOption(productCode);
    }

    @Override
    public List<DataItemOption> listGroupOption() {
        return isvDataMapper.listGroupOption();
    }

    @Override
    public List<DataItemOption> listDeviceOptions(String productCode) {
        return isvDataMapper.listDeviceOptions(productCode);
    }

    @Override
    public List<DeviceStatusCountDto> countDeviceStatus(String status, String category, String range, List<String> values) {
        if(Objects.equals(category, "range")) {
            return isvDataMapper.countDeviceStatusByRange(status, range, values);
        } else { // void
            return isvDataMapper.countDeviceStatus(status, range, values);
        }
    }

    @Override
    public GlobalApiParams countAggRealtimeOfGlobal(GlobalApiParams params) {
        return isvDataMapper.countAggRealtimeOfGlobal(params);
    }

    @Override
    public GlobalApiParams countDeviceStatusInfoOfGlobal(GlobalApiParams params) {
        return isvDataMapper.countDeviceStatusInfoOfGlobal(params);
    }

    @Override
    public GlobalApiParams countDeviceWarnOfGlobal(GlobalApiParams req) {
        return isvDataMapper.countDeviceWarnOfGlobal(req);
    }

    @Override
    public GlobalApiParams countBasicProductInfoOfGlobal(GlobalApiParams req) {
        return isvDataMapper.countBasicProductInfoOfGlobal(req);
    }

    @Override
    public GlobalApiParams countBasicDriverInfoOfGlobal(GlobalApiParams req) {
        return isvDataMapper.countBasicDriverInfoOfGlobal(req);
    }

    @Override
    public void listBasicProduct(Page<?> page, Map<String, Object> params) {
        isvDataMapper.listBasicProduct(page, params);
        if(CollectionUtils.isNotEmpty(page.getRecords())) {
            page.getRecords().forEach(item -> {
                Map<String, String> map = (Map<String, String>) item;
                String deviceType = map.get("deviceType");
                if(deviceType != null) {
                    deviceType = deviceType.equals("Direct") ? "直连设备" : deviceType.equals("Gateway") ? "边缘网关" : "网关子设备";
                    map.put("deviceType", deviceType);
                }
            });
        }
    }
}
