package com.iteaj.iboot.plugin.iotview.api;

import com.iteaj.framework.spi.iot.view.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class ModelAttrTableApiItem implements ViewTableApiItem {

    private static final String KEY = "GTA";
    private static final String NAME = "表格接口";

    private List<DataItemOption> options = Collections.singletonList(new DataItemOption(NAME, KEY));

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public GlobalApiParams fetch(GlobalApiParams params) {
        return null;
    }

    @Override
    public List<DataItemOption> basicOptions() {
        return options;
    }

}
