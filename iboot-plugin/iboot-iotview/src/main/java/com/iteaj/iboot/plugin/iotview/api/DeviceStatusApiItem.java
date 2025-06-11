package com.iteaj.iboot.plugin.iotview.api;

import com.iteaj.framework.spi.iot.view.DataItemOption;
import com.iteaj.framework.spi.iot.view.ViewApiItem;
import com.iteaj.framework.spi.iot.view.ViewApiParams;
import com.iteaj.iboot.plugin.iotview.service.ISVDataService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DeviceStatusApiItem implements ViewApiItem {

    private static final String KEY = "DSS";
    private static final String NAME = "设备状态";
    private final ISVDataService isvDataService;

    private List<DataItemOption> options = Arrays.asList(new DataItemOption("设备状态", "STATUS")
            .addChild(new DataItemOption("在线", "online")
                    .addChild(new DataItemOption("统计", "count", true, true))
                    .addChild(new DataItemOption("占比", "ratio", true, true)))
            .addChild(new DataItemOption("离线", "offline")
                    .addChild(new DataItemOption("统计", "count", true, true))
                    .addChild(new DataItemOption("占比", "ratio", true, true))));

    public DeviceStatusApiItem(ISVDataService isvDataService) {
        this.isvDataService = isvDataService;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getOrder() {
        return ViewApiItem.super.getOrder() - 80;
    }

    @Override
    public Object fetch(ViewApiParams params) {
        return null;
    }

    @Override
    public List<DataItemOption> basicOptions() {
        return options;
    }

    @Override
    public List<DataItemOption> loadOptions(String[] args) {
        return null;
    }
}
