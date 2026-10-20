package com.iteaj.iboot.plugin.iotview.api.table;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.spi.iot.data.RealtimeCollectData;
import com.iteaj.framework.spi.iot.data.RealtimeCollectDataService;
import com.iteaj.framework.spi.iot.data.RealtimeDataParam;
import com.iteaj.framework.spi.iot.view.DataItemOption;
import com.iteaj.framework.spi.iot.view.ViewApi;
import com.iteaj.framework.spi.iot.view.table.TableApiConfig;
import com.iteaj.framework.spi.iot.view.table.TableApiItem;
import com.iteaj.framework.spi.iot.view.table.TableApiParams;
import com.iteaj.framework.spi.iot.view.table.TableApiResult;
import com.iteaj.iboot.plugin.iotview.service.ISVDataService;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProductDataTableApiItem implements TableApiItem {

    private static final String KEY = "PDTA";
    private static final String NAME = "产品数据";

    private final ISVDataService svDataService;
    private final RealtimeCollectDataService collectDataService;
    private List<DataItemOption> options = Collections.singletonList(new DataItemOption(NAME, KEY));

    public ProductDataTableApiItem(ISVDataService svDataService, RealtimeCollectDataService collectDataService) {
        this.svDataService = svDataService;
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
    public TableApiResult fetch(TableApiParams params) {
        String productCode = params.getKey(1);
        Page<RealtimeCollectData> page = new Page<>(params.getCurrent(), params.getSize());
        String uid = params.getParam("uid");
        String groupId = params.getParam("groupId");
        String startTime = params.getParam("startTime");
        String endTime = params.getParam("endTime");

        PageResult<Page> result = collectDataService.productOfPage(page, new RealtimeDataParam()
                .setEndTime(endTime).setStartTime(startTime).setProductCode(productCode).setUid(uid)
                .setDeviceGroupId(StrUtil.isNotEmpty(groupId) ? Long.parseLong(groupId) : null));
        if(CollectionUtil.isNotEmpty(page.getRecords())) {
            List<Map<String, String>> records = new ArrayList<>();
            page.getRecords().forEach(record -> {
                Map<String, String> map = new HashMap<>();
                map.put("deviceSn", record.getDeviceSn());
                map.put("deviceName", record.getDeviceName());
                map.put("collectTime", DateUtil.format(record.getCollectTime(), "yyyy-MM-dd HH:mm:ss"));
                Object value = record.getValue();
                if(value != null && !value.toString().isEmpty()) {
                    String[] split = value.toString().split(":::");
                    for(String item : split) {
                        String[] items = item.split("=");
                        map.put(items[0], items[1]);
                    }
                }

                records.add(map);
            });

            result.getData().setRecords(records);
        }

        return new TableApiResult(page.getTotal(), page.getRecords());
    }

    @Override
    public List<DataItemOption> basicOptions() {
        return options;
    }

    @Override
    public ViewApi init() {
        DataItemOption parent = options.get(0);
        svDataService.listProductOption(false)
                .forEach(option -> {
                    List<DataItemOption> itemOptions = svDataService.listModelAttrOption(option.getValue());
                    if(CollectionUtil.isNotEmpty(itemOptions)) {
                        TableApiConfig apiConfig = new TableApiConfig()
                                .addReqParams("设备UID", "uid", "")
                                .addReqParams("所属分组", "groupId", "设备所属分组id")
                                .addReqParams("开始时间", "startTime", "yyyy-MM-dd HH:mm:ss")
                                .addReqParams("结束时间", "endTime", "yyyy-MM-dd HH:mm:ss")
                                .addTableHead("采集时间", "collectTime", 140)
                                .addTableHead("设备名称", "deviceName", 120, true, true)
                                .addTableHead("设备编号", "deviceSn", 120, true, true);
                        itemOptions.forEach(itemOption ->
                                apiConfig.addTableHead(itemOption.getLabel(), itemOption.getValue()));
                        option.setConfig(apiConfig);
                    }

                    parent.addChild(option.setLeaf(true));
                });
        return TableApiItem.super.init();
    }
}
