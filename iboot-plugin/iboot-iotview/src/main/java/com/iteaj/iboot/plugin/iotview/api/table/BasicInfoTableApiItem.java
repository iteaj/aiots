package com.iteaj.iboot.plugin.iotview.api.table;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.spi.iot.view.*;
import com.iteaj.framework.spi.iot.view.table.TableApiConfig;
import com.iteaj.framework.spi.iot.view.table.TableApiItem;
import com.iteaj.framework.spi.iot.view.table.TableApiParams;
import com.iteaj.framework.spi.iot.view.table.TableApiResult;
import com.iteaj.iboot.plugin.iotview.service.ISVDataService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class BasicInfoTableApiItem implements TableApiItem {

    private static final String KEY = "BITA";
    private static final String NAME = "基础列表";

    private final ISVDataService svDataService;

    private List<DataItemOption> options = Collections.singletonList(
            new DataItemOption(NAME, KEY)
                    .builder().addChild("产品信息", "product", true
                            , new TableApiConfig()
                                    .addTableHead("产品名称", "name")
                                    .addTableHead("设备类型", "deviceType")
                                    .addTableHead("产品编号", "code", 180)
                                    .addTableHead("产品状态", "status", 80)
                                    .addTableHead("设备数量", "deviceNum", 100)
                                    .addTableHead("在线数量", "onlineNum", 100)
                                )
                    .toParent().addChild("设备信息", "device", true
                            , new TableApiConfig()
                                    .addTableHead("设备名称", "name")
                                    .addTableHead("uid", "uid"))
                    .toTop().build()
    );

    public BasicInfoTableApiItem(ISVDataService svDataService) {
        this.svDataService = svDataService;
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
    public TableApiResult fetch(TableApiParams params) {
        String func = params.getKey(1);
        Page<?> page = new Page<>(params.getCurrent(), params.getSize());
        if(Objects.equals("product", func)) {
            svDataService.listBasicProduct(page, params.getParams());
        }

        return new TableApiResult(page.getTotal(), page.getRecords());
    }

    @Override
    public List<DataItemOption> basicOptions() {
        return options;
    }

}
