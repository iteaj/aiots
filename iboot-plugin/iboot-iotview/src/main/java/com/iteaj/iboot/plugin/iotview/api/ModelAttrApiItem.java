package com.iteaj.iboot.plugin.iotview.api;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.spi.iot.view.DataItemOption;
import com.iteaj.framework.spi.iot.view.ViewApiItem;
import com.iteaj.framework.spi.iot.view.ViewApiParams;
import com.iteaj.iboot.plugin.iotview.service.ISVDataService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class ModelAttrApiItem implements ViewApiItem {

    private static final String KEY = "MODEL";
    private static final String NAME = "模型属性";
    private final ISVDataService isvDataService;

    private List<DataItemOption> calcOptions = Arrays.asList(
            new DataItemOption("数量统计", "count", true),
            new DataItemOption("求平均值", "avg", true),
            new DataItemOption("求最大值", "max", true),
            new DataItemOption("求最小值", "min", true)
    );
    private List<DataItemOption> options = Arrays.asList(new DataItemOption(NAME, KEY));

    public ModelAttrApiItem(ISVDataService isvDataService) {
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
    public Object fetch(ViewApiParams params) {
        return null;
    }

    @Override
    public int getOrder() {
        return ViewApiItem.super.getOrder() + 200;
    }

    @Override
    public ViewApiItem init() {
        final DataItemOption parent = options.get(0);
        final List<DataItemOption> dataItemOptions = isvDataService.listProductOption();
        if(CollectionUtil.isNotEmpty(dataItemOptions)) {
            dataItemOptions.forEach(parent::addChild);
        }
        return this;
    }

    @Override
    public List<DataItemOption> basicOptions() {
        return options;
    }

    @Override
    public List<DataItemOption> loadOptions(String[] args) {
        final List<DataItemOption> dataItemOptions = isvDataService.listModelAttrOption(args[1]);
        if(CollectionUtil.isNotEmpty(dataItemOptions)) {
            dataItemOptions.forEach(item -> {
                calcOptions.forEach(calc -> item.addChild(calc));
            });
        } else {
            return Collections.emptyList();
        }

        return dataItemOptions;
    }
}
