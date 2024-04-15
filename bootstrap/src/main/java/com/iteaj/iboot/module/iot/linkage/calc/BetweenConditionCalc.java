package com.iteaj.iboot.module.iot.linkage.calc;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.iteaj.iboot.module.iot.linkage.ConditionCalc;

import java.util.Date;

public class BetweenConditionCalc implements ConditionCalc {

    @Override
    public String getName() {
        return "两者之间";
    }

    @Override
    public String getOperational() {
        return "between";
    }

    @Override
    public String getDesc() {
        return "([0,3])";
    }

    @Override
    public boolean calc(Object value, Object condition) {
        String conValue = condition.toString();
        JSONArray array = JSONArray.parseArray(condition.toString());
        if(value instanceof Date) {
            Date[] timestamp = new Date[2];
            boolean timeItem = conValue.contains(":");
            boolean dateItem = conValue.contains("-");
            if(dateItem && timeItem) { // yyyy-MM-dd HH:mm:ss
                timestamp[0] = DateUtil.parse(array.getString(0), DatePattern.NORM_DATETIME_PATTERN);
                timestamp[1] = DateUtil.parse(array.getString(1), DatePattern.NORM_DATETIME_PATTERN);
            } else if(dateItem) {
                timestamp[0] = DateUtil.parse(array.getString(0), DatePattern.NORM_DATE_PATTERN);
                timestamp[1] = DateUtil.parse(array.getString(1), DatePattern.NORM_DATE_PATTERN);
            } else if(timeItem) {
                timestamp[0] = DateUtil.parse(array.getString(0), DatePattern.NORM_TIME_FORMAT);
                timestamp[1] = DateUtil.parse(array.getString(1), DatePattern.NORM_TIME_FORMAT);
            } else {
                return false;
            }

            return new DateTime((Date) value).isIn(timestamp[0], timestamp[1]);
        }

        Double aDouble = Double.valueOf(value.toString());
        return aDouble >= array.getDoubleValue(0) && aDouble <= array.getDoubleValue(1);
    }
}
