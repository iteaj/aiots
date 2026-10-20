package com.iteaj.iboot.plugin.iotview.api.data;

import cn.hutool.core.date.DateUtil;
import com.iteaj.framework.spi.iot.view.DataItemOption;
import com.iteaj.framework.spi.iot.view.ApiConfigOption;
import com.iteaj.framework.spi.iot.view.global.GlobalApi;
import com.iteaj.framework.spi.iot.view.global.GlobalApiParams;
import com.iteaj.iboot.plugin.iotview.service.ISVDataService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
public class DeviceGlobalApi implements GlobalApi {

    private static final String KEY = "DSG";
    private static final String NAME = "设备信息";
    private final ISVDataService isvDataService;

    private ApiConfigOption statusConfig = new ApiConfigOption()
            .addReqParams("产品编码", "productCode", "不填统计所有产品")
            .addReqParams("设备分组ID", "deviceGroupId", "不填统计所有设备组")
            .addRespParams("总设备数", "total", "当前系统的总设备数")
            .addRespParams("在线数", "online", "当前系统在线的设备数")
            .addRespParams("离线数", "offline", "当前系统离线的设备数")
            .addRespParams("在线占比", "onlineRate", "当前系统在线数占总设备的比例")
            .addRespParams("离线占比", "offlineRate", "当前系统离线数占总设备的比例");

    private ApiConfigOption warnConfig = new ApiConfigOption()
            .addReqParams("产品编码", "productCode", "不填统计所有产品")
//            .addReqParams("设备分组ID", "deviceGroupId", "不填统计所有设备组")
            .addReqParams("设备uid", "uid", "不填统计所有设备")
            .addRespParams("当月值", "month", "统计当月的告警数")
            .addRespParams("当天值", "day", "统计当天的告警数")
            .addRespParams("近一小时值", "hour", "统计近一小时的告警数");


    private List<DataItemOption> options = Arrays.asList(
            new DataItemOption(NAME, KEY).builder()
                    .addChild("状态信息", "status", statusConfig)
                        .addChild("统计", "count", true)
                .toTop()
                    .addChild("告警信息", "warn")
                        .addChild("统计", "count", true, warnConfig)
                .toTop().build()
    );

    public DeviceGlobalApi(ISVDataService isvDataService) {
        this.isvDataService = isvDataService;
    }

    @Override
    public GlobalApiParams fetch(GlobalApiParams params) {
        final String func = params.resolveKeyItem(1);
        if(Objects.equals("status", func)) {
            return doFetchDeviceStatusInfo(params);
        } else if(Objects.equals("warn", func)) {
            return doFetchDeviceWarn(params);
        }

        return null;
    }

    private GlobalApiParams doFetchDeviceWarn(GlobalApiParams req) {
        final Date date = new Date();
        final long month = DateUtil.beginOfMonth(date).getTime() / 1000;
        final long day = DateUtil.beginOfDay(date).getTime() / 1000;
        final long hour = DateUtil.beginOfHour(date).getTime() / 1000;
        req.set("month", month).set("day", day).set("hour", hour);
        GlobalApiParams resp = isvDataService.countDeviceWarnOfGlobal(req);
        if(resp == null) {
            resp = new GlobalApiParams();
            resp.set("month", 0).set("day", 0).set("hour", 0);
        }

        return resp;
    }

    private GlobalApiParams doFetchDeviceStatusInfo(GlobalApiParams params) {
        GlobalApiParams resp = isvDataService.countDeviceStatusInfoOfGlobal(params);
        final Object total = resp.get("total");
        if(total == null) {
            resp.put("total", 0);
            resp.put("online", 0);
            resp.put("offline", 0);
            resp.put("onlineRate", 0);
            resp.put("offlineRate", 0);
        } else {
            final int totalValue = Integer.parseInt(total.toString());
            final int online = Integer.parseInt(resp.get("online").toString());
            final BigDecimal onlineRate = BigDecimal.valueOf(online / (totalValue * 1.0)).setScale(2, BigDecimal.ROUND_DOWN);
            resp.put("onlineRate", onlineRate);
            resp.put("offlineRate", 1 - onlineRate.doubleValue());
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
