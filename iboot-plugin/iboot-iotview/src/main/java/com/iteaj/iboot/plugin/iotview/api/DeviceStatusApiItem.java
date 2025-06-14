package com.iteaj.iboot.plugin.iotview.api;

import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.view.*;
import com.iteaj.iboot.plugin.iotview.dto.DeviceStatusCountDto;
import com.iteaj.iboot.plugin.iotview.service.ISVDataService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DeviceStatusApiItem implements ViewApiItem {

    private static final String KEY = "DSS";
    private static final String NAME = "设备状态";
    private final ISVDataService isvDataService;

    private List<DataItemOption> options = Arrays.asList(new DataItemOption("设备状态", KEY)
            .asName().builder()
            .addNameChild("在线", "online")
                .addNameChild("统计", "count", true)
            .toParent()
                .addNameChild("占比", "ratio", true)
            .toTop()
            .addNameChild("离线", "offline")
                .addNameChild("统计", "count", true)
            .toParent()
                .addNameChild("占比", "ratio", true)
            .toTop()
            .build()
    );

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
    public List<ViewFetchResult> fetch(List<ViewApiParams.Value> values, ViewApiParams params) {
        List<ViewFetchResult> results = new ArrayList<>(values.size());
        values.stream().forEach(item -> {

            final String rangeConfig = item.getRangeValue();
            if(StrUtil.isBlank(rangeConfig)) {
                throw new ServiceException("未指定统计范围");
            }
            final String[] split = rangeConfig.split(DataItemOption.SPLIT);
            String range = split[0]; String rangeValue = null;
            if(split.length > 1) {
                rangeValue = split[1];
            }
            final String status = item.getValueItem(1);
            final String countType = item.getValueItem(2);

            DeviceStatusCountDto num = isvDataService.countDeviceStatus(status, range, rangeValue);
            if("count".equals(countType)) {
                results.add(new NumberFetchResult(item.getItem(), num.getTotal()));
            } else if("ratio".equals(countType)) {
                results.add(new NumberFetchResult(item.getItem()
                        , new BigDecimal(num.getValue() / (num.getTotal() * 1.0))
                        .setScale(2, BigDecimal.ROUND_DOWN)));
            } else {

            }

        });
        return results;
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
