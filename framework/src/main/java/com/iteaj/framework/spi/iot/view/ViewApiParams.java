package com.iteaj.framework.spi.iot.view;

import com.iteaj.framework.spi.iot.consts.TimeCountType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ViewApiParams {

    /**
     * chart类型
     */
    private String chart;

    /**
     * 时间类型
     * @see #timeRange
     */
    private TimeCountType timeType;

    /**
     * 时间范围(格式 yyyy-MM-dd HH:mm)
     * 1. 支持具体的时间 如：2024-01-10 15:00 - 2024-01-16 15:00
     * 2. 支持now关键字 如：now-6 - now
     * @see #timeType
     */
    private List<String> timeRange;

    /**
     * 值条件
     */
    private List<Value> values;

    @Getter
    @Setter
    public static class Value {

        /**
         * 数据项
         */
        private String item;

        /**
         * 字段
         */
        private String field;

        /**
         * 计算类型(count, min, max, avg, sum, ...)
         */
        private String calcType;

        /**
         * 统计范围
         * all 所有
         * product 产品
         * group 设备组
         * device 指定设备
         */
        private String range;

        /**
         * 范围值
         */
        private String rangeValue;

    }
}
