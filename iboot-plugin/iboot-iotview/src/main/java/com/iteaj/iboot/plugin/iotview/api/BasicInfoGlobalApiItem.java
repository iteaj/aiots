package com.iteaj.iboot.plugin.iotview.api;

import com.iteaj.framework.spi.iot.view.DataItemOption;
import com.iteaj.framework.spi.iot.view.GlobalApiConfig;
import com.iteaj.framework.spi.iot.view.GlobalApiItem;
import com.iteaj.framework.spi.iot.view.GlobalApiParams;
import com.iteaj.iboot.plugin.iotview.service.ISVDataService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class BasicInfoGlobalApiItem implements GlobalApiItem {

    private static final String KEY = "BIG";
    private static final String NAME = "基础信息";
    private final ISVDataService isvDataService;

    private GlobalApiConfig driverConfig = new GlobalApiConfig()
            .addRespParams("总驱动数", "total", "平台现有的驱动数量")
            .addRespParams("启用数", "start", "平台已经启用的驱动数量")
            .addRespParams("启用占比", "startRate", "平台启用的占总驱动的比例");

    private GlobalApiConfig productConfig = new GlobalApiConfig()
            .addRespParams("总产品数", "total", "平台现有的产品数量")
            .addRespParams("启用数", "start", "平台已经启用的产品数量")
            .addRespParams("启用占比", "startRate", "平台启用的占总产品的比例");

    private List<DataItemOption> options = Arrays.asList(
            new DataItemOption(NAME, KEY).builder()
                    .addChild("设备驱动", "driver", true, driverConfig)
                .toParent()
                    .addChild("产品信息", "product", true, productConfig)
            .toTop().build()
    );

    public BasicInfoGlobalApiItem(ISVDataService isvDataService) {
        this.isvDataService = isvDataService;
    }

    @Override
    public GlobalApiParams fetch(GlobalApiParams params) {
        final String func = params.resolveKeyItem(1);
        if(Objects.equals("driver", func)) {
            return doFetchDriverCountInfo(params);
        } else if(Objects.equals("product", func)) {
            return doFetchProductCountInfo(params);
        }
        return null;
    }

    private GlobalApiParams doFetchProductCountInfo(GlobalApiParams req) {
        GlobalApiParams resp = isvDataService.countBasicProductInfoOfGlobal(req);
        return globalApiRespHandle(resp);
    }

    private GlobalApiParams doFetchDriverCountInfo(GlobalApiParams req) {
        GlobalApiParams resp = isvDataService.countBasicDriverInfoOfGlobal(req);
        return globalApiRespHandle(resp);
    }

    private GlobalApiParams globalApiRespHandle(GlobalApiParams resp) {
        if(resp == null) {
            resp = new GlobalApiParams().set("total", 0).set("start", 0).set("startRate", 0);
        } else {
            final int start = Integer.parseInt(resp.get("start").toString());
            final int total = Integer.parseInt(resp.get("total").toString());
            resp.set("startRate", new BigDecimal(start / (total * 1.0)).setScale(2, BigDecimal.ROUND_DOWN));
        }

        return resp;
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
    public List<DataItemOption> basicOptions() {
        return options;
    }
}
