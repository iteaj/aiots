package com.iteaj.iboot.plugin.iotview.api;

import cn.hutool.core.date.DateUtil;
import com.iteaj.framework.spi.iot.view.DataItemOption;
import com.iteaj.framework.spi.iot.view.ApiConfigOption;
import com.iteaj.framework.spi.iot.view.global.GlobalApiItem;
import com.iteaj.framework.spi.iot.view.global.GlobalApiParams;
import com.iteaj.iboot.plugin.iotview.service.ISVDataService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
public class ModelAttrGlobalApiItem implements GlobalApiItem {

    private static final String KEY = "MAG";
    private static final String NAME = "采集信息";
    private final ISVDataService isvDataService;
    private ApiConfigOption apiConfig = new ApiConfigOption()
            .addReqParams("产品编码", "productCode", "不填统计所有产品")
            .addReqParams("设备uid", "uid", "不填统计所有设备")
            .addReqParams("设备分组ID", "deviceGroupId", "不填统计所有设备组")
            .addRespParams("当年值", "year", "统计当年的聚合值")
            .addRespParams("当季值", "quarter", "统计当季的聚合值")
            .addRespParams("当月值", "month", "统计当月的聚合值")
            .addRespParams("当天值", "day", "统计当天的聚合值")
            .addRespParams("近一小时值", "hour", "统计近一小时的聚合值");

    private List<DataItemOption> options = Arrays.asList(
            new DataItemOption(NAME, KEY).builder()
                    .addChild("聚合函数", "agg", apiConfig)
                        .addChild("数量", "count", true).toParent()
                        .addChild("求和", "sum", true).toParent()
                        .addChild("平均值", "avg", true).toParent()
                        .addChild("最大值", "max", true).toParent()
                        .addChild("最小值", "min", true)
                        .toTop().build()
    );

    public ModelAttrGlobalApiItem(ISVDataService isvDataService) {
        this.isvDataService = isvDataService;
    }

    @Override
    public GlobalApiParams fetch(GlobalApiParams params) {
        final Date date = new Date();
        final String subFunc = params.resolveKeyItem(1);

        if(Objects.equals("agg", subFunc)) {
            return doAggFetch(params, date);
        }
        return new GlobalApiParams()
                .set("year", "78/年")
                .set("month", "58/月")
                .set("day", "38/日")
                .set("hour", "8");
    }

    private GlobalApiParams doAggFetch(GlobalApiParams params, Date date) {
        final long year = DateUtil.beginOfYear(date).getTime() / 1000;
        final long month = DateUtil.beginOfMonth(date).getTime() / 1000;
        final long day = DateUtil.beginOfDay(date).getTime() / 1000;
        final long hour = DateUtil.beginOfHour(date).getTime() / 1000;
        final long quarter = DateUtil.beginOfQuarter(date).getTime() / 1000;
        params.put("year", year);
        params.put("month", month);
        params.put("day", day);
        params.put("hour", hour);
        params.put("quarter", quarter);
        params.put("type", params.resolveKeyItem(2));
        GlobalApiParams response = isvDataService.countAggRealtimeOfGlobal(params);
        if(response != null) {
            final Object year1 = response.get("year");
            if(year1 == null) {
                response.put("year", 0);
            } else {
                response.put("year", new BigDecimal(year1.toString()).setScale(2, BigDecimal.ROUND_DOWN));
            }
            final Object month1 = response.get("month");
            if(month1 == null) {
                response.put("month", 0);
            } else {
                response.put("month", new BigDecimal(month1.toString()).setScale(2, BigDecimal.ROUND_DOWN));
            }
            final Object day1 = response.get("day");
            if(day1 == null) {
                response.put("day", 0);
            } else {
                response.put("day", new BigDecimal(day1.toString()).setScale(2, BigDecimal.ROUND_DOWN));
            }
            final Object hour1 = response.get("hour");
            if(hour1 == null) {
                response.put("hour", 0);
            } else {
                response.put("hour", new BigDecimal(hour1.toString()).setScale(2, BigDecimal.ROUND_DOWN));
            }
            final Object quarter1 = response.get("quarter");
            if(quarter1 == null) {
                response.put("quarter", 0);
            } else {
                response.put("quarter", new BigDecimal(quarter1.toString()).setScale(2, BigDecimal.ROUND_DOWN));
            }
        } else {
            response = new GlobalApiParams();
            params.put("year", 0);
            params.put("month", 0);
            params.put("day", 0);
            params.put("hour", 0);
            params.put("quarter", 0);
        }

        return response;
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
