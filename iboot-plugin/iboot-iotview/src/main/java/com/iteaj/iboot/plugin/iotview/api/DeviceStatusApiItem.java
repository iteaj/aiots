package com.iteaj.iboot.plugin.iotview.api;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.data.BaseEchartsCount;
import com.iteaj.framework.spi.iot.view.*;
import com.iteaj.framework.spi.iot.view.chart.*;
import com.iteaj.iboot.plugin.iotview.dto.DeviceStatusCountDto;
import com.iteaj.iboot.plugin.iotview.service.ISVDataService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class DeviceStatusApiItem implements ChartApiItem {

    private static final String KEY = "DSS";
    private static final String NAME = "设备状态";
    private final ISVDataService isvDataService;

    private List<DataItemOption> options = Arrays.asList(new DataItemOption("设备状态", KEY).asName().builder()
            .addNameChild("总设备数", "all", true)
            .toParent()
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
        return ChartApiItem.super.getOrder() - 80;
    }

    @Override
    public List<ChartFetchResult> fetch(List<ChartApiParams.Value> values, ChartApiParams params) {

        if(StrUtil.isBlank(params.getRange())) {
            throw new ServiceException("未指定统计范围配置");
        }
        if(StrUtil.isBlank(params.getCategory())) {
            throw new ServiceException("未指定产品轴配置");
        }

        if(params.isTable()) {
            throw new ServiceException("设备状态不支持表格组件");
        }

        List<ChartFetchResult> results = new ArrayList<>(values.size());
        if(Objects.equals("range", params.getCategory())) {
            Map<String, ListFetchResult> maps = new LinkedHashMap<>();
            values.stream().forEach(item -> {
                final String status = item.getValueItem(1);
                final String countType = item.getValueItem(2);

                List<DeviceStatusCountDto> countDtos = isvDataService.countDeviceStatus(status
                        , params.getCategory(), params.getRange(), params.getRanges());
                if(CollectionUtil.isNotEmpty(countDtos)) {
                    countDtos.forEach(countDto -> {
                        ListFetchResult listFetchResult = maps.get(item.getItem());
                        if(listFetchResult == null) {
                            maps.put(item.getItem(), listFetchResult = new ListFetchResult(item.getItem(), new ArrayList<>()));
                        }

                        if("count".equals(countType)) {
                            listFetchResult.addValue(new BaseEchartsCount(countDto.getName(), countDto.getValue()));
                        } else if("ratio".equals(countType)) {
                            listFetchResult.addValue(new BaseEchartsCount(countDto.getName()
                                    , new BigDecimal(countDto.getValue() / (countDto.getTotal() * 1.0))
                                    .setScale(2, BigDecimal.ROUND_DOWN)));
                        } else {
                            listFetchResult.addValue(new BaseEchartsCount(countDto.getName(), countDto.getTotal()));
                        }
                    });
                }
            });

            results.addAll(maps.values());
        } else if(Objects.equals("time", params.getCategory())) {
            throw new ServiceException("不支持按时间统计");
        } else {
            values.stream().forEach(item -> {
                final String status = item.getValueItem(1);
                final String countType = item.getValueItem(2);

                List<DeviceStatusCountDto> countDtos = isvDataService.countDeviceStatus(status
                        , params.getCategory(), params.getRange(), params.getRanges());
                final DeviceStatusCountDto countDto = countDtos.get(0);
                if ("count".equals(countType)) {
                    results.add(new NumberFetchResult(item.getItem(), countDto.getValue()));
                } else if ("ratio".equals(countType)) {
                    results.add(new NumberFetchResult(item.getItem()
                            , new BigDecimal(countDto.getValue() / (countDto.getTotal() * 1.0))
                            .setScale(2, BigDecimal.ROUND_DOWN)));
                } else {
                    results.add(new NumberFetchResult(item.getItem(), countDto.getTotal()));
                }
            });
        }

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
