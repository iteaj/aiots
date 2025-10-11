package com.iteaj.iboot.plugin.iotview.api;

import com.iteaj.framework.spi.iot.view.DataItemOption;
import com.iteaj.framework.spi.iot.view.ViewApiParams;
import com.iteaj.framework.spi.iot.view.ViewFetchResult;
import com.iteaj.framework.spi.iot.view.ViewTableApiItem;

import java.util.Arrays;
import java.util.List;

public class ModelAttrTableApiItem implements ViewTableApiItem {
    private static final String KEY = "MT";
    private static final String NAME = "模型属性";

    private List<DataItemOption> options = Arrays.asList(new DataItemOption(NAME, KEY));

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<ViewFetchResult> fetch(List<ViewApiParams.Value> values, ViewApiParams params) {
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
