package com.iteaj.iboot.plugin.iotview.service.impl;

import com.iteaj.framework.spi.iot.view.DataItemOption;
import com.iteaj.iboot.plugin.iotview.dto.DeviceStatusCountDto;
import com.iteaj.iboot.plugin.iotview.mapper.ISVDataMapper;
import com.iteaj.iboot.plugin.iotview.service.ISVDataService;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return null;
    }

    @Override
    public DeviceStatusCountDto countDeviceStatus(String status, String range, String rangeValue) {
        return isvDataMapper.countDeviceStatus(status, range, rangeValue);
    }
}
