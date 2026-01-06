package com.iteaj.iboot.plugin.iotview.api.chart;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.data.BaseEchartsCount;
import com.iteaj.framework.spi.iot.data.RealtimeCollectDataService;
import com.iteaj.framework.spi.iot.view.*;
import com.iteaj.framework.spi.iot.view.chart.*;
import com.iteaj.iboot.plugin.iotview.service.ISVDataService;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ModelAttrApiItem implements ChartApiItem {

    private static final String KEY = "MODEL";
    private static final String NAME = "模型属性";
    private final ISVDataService isvDataService;
    private final RealtimeCollectDataService collectDataService;
    private List<DataItemOption> calcOptions = Arrays.asList(
            new DataItemOption("求和", "sum", true).asName(),
            new DataItemOption("数量统计", "count", true).asName(),
            new DataItemOption("求平均值", "avg", true).asName(),
            new DataItemOption("求最大值", "max", true).asName(),
            new DataItemOption("求最小值", "min", true).asName()
    );
    private List<DataItemOption> options = Arrays.asList(new DataItemOption(NAME, KEY));

    public ModelAttrApiItem(ISVDataService isvDataService, RealtimeCollectDataService collectDataService) {
        this.isvDataService = isvDataService;
        this.collectDataService = collectDataService;
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
        return ChartApiItem.super.getOrder() + 200;
    }

    @Override
    public List<ChartFetchResult> fetch(List<ChartApiParams.Value> values, ChartApiParams params) {
        if(StrUtil.isBlank(params.getRange())) {
            throw new ServiceException("未指定统计范围配置");
        }

        if(StrUtil.isBlank(params.getCategory())) {
            throw new ServiceException("未指定产品轴配置");
        }

        if(CollectionUtil.isEmpty(params.getTimeRange())) {
            throw new ServiceException("未指定时间范围");
        }

        List<ChartFetchResult> results = new ArrayList<>(values.size());

        values.forEach(item -> {
            final String productCode = item.getValueItem(1);
            final String attrField = item.getValueItem(2);
            final String calcType = item.getValueItem(3);
            final ChartSqlCondition condition = ChartSqlCondition.build(productCode, attrField, calcType, params);
            List<BaseEchartsCount> countValues = collectDataService
                    .countOfView(condition);
            if(Objects.equals("time", params.getCategory())) {
                countValues = condition.buildTimeValues(countValues);
            }
            results.add(new ListFetchResult(item.getItem(), countValues));
        });

        return results;
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
