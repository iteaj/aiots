package com.iteaj.iboot.module.iot.linkage;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.Objects;

public interface ConditionCalc {

    /**
     * 运算符名称
     * @return
     */
    String getName();

    /**
     * 运算符说明
     * @return
     */
    default String getDesc() {
        return "";
    }

    /**
     * 获取运算符名称
     * @return
     */
    String getOperational();

    /**
     * 计算
     * @param value 当前值
     * @param condition 条件值
     * @return
     */
    boolean calc(Object value, Object condition);

    default Date resolverDate(Object value, Object condition) {
        if(value instanceof Date) {
            String dateStr = condition.toString();
            boolean timeItem = dateStr.contains(":");
            boolean dateItem = dateStr.contains("-");
            if(dateItem && timeItem) { // yyyy-MM-dd HH:mm:ss
                return DateUtil.parse(dateStr, DatePattern.NORM_DATETIME_PATTERN);
            } else if(dateItem) {
                return DateUtil.parse(dateStr, DatePattern.NORM_DATE_PATTERN);
            } else if(timeItem) {
                return DateUtil.parse(dateStr, DatePattern.NORM_TIME_FORMAT);
            } else {
                throw new IllegalArgumentException("日期解析异常["+dateStr+"]");
            }
        }
        return null;
    }
}
