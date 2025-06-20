package com.iteaj.iboot.plugin.iotview.api;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.spi.iot.data.BaseEchartsCount;
import com.iteaj.framework.spi.iot.view.*;
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
            new DataItemOption("数量统计", "count", true).asName(),
            new DataItemOption("求平均值", "avg", true).asName(),
            new DataItemOption("求最大值", "max", true).asName(),
            new DataItemOption("求最小值", "min", true).asName()
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
    public int getOrder() {
        return ViewApiItem.super.getOrder() + 200;
    }

    @Override
    public List<ViewFetchResult> fetch(List<ViewApiParams.Value> values, ViewApiParams params) {
        return Arrays.asList(new ListFetchResult("温度", Arrays.asList(
                new BaseEchartsCount("2025-01", 30),
                new BaseEchartsCount("2025-02", 50),
                new BaseEchartsCount("2025-03", 70),
                new BaseEchartsCount("2025-04", 90),
                new BaseEchartsCount("2025-05", 88)
                )
            )
            , new ListFetchResult("湿度", Arrays.asList(
                    new BaseEchartsCount("2025-01", 28),
                    new BaseEchartsCount("2025-02", 54),
                    new BaseEchartsCount("2025-03", 42),
                    new BaseEchartsCount("2025-04", 19),
                    new BaseEchartsCount("2025-05", 38)
            )));
    }

    @Override
    public List<DataItemOption> basicOptions() {
        return options;
    }

    @Override
    public List<DataItemOption> loadOptions(String[] args) {
        List<DataItemOption> results = Collections.emptyList();
        if(args.length == 1) {
            final List<DataItemOption> dataItemOptions = isvDataService.listProductOption(false);
            if(CollectionUtil.isNotEmpty(dataItemOptions)) {
                results = dataItemOptions;
                dataItemOptions.forEach(child -> child.setValue(KEY + DataItemOption.SPLIT + child.getValue()).asName());
            }
        } else if(args.length == 2) {
            final List<DataItemOption> dataItemOptions = isvDataService.listModelAttrOption(args[1]);
            if(CollectionUtil.isNotEmpty(dataItemOptions)) {
                results = dataItemOptions;
                final String keyPrefix = String.join(DataItemOption.SPLIT, args);
                dataItemOptions.forEach(item -> {
                    item.setValue(keyPrefix + DataItemOption.SPLIT + item.getValue());
                    calcOptions.forEach(calc -> {
                        try {
                            item.asName().addChild(calc.clone());
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    });
                });
            }
        } else {

        }

        return results;
    }
}
