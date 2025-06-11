package com.iteaj.iboot.plugin.iotview.service.impl;

import com.iteaj.framework.spi.iot.view.DataItemOption;
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
    public List<DataItemOption> listProductOption() {
        return isvDataMapper.listProductOption();
    }

    @Override
    public List<DataItemOption> listModelAttrOption(String productId) {
        return isvDataMapper.listModelAttrOption(productId);
    }
}
