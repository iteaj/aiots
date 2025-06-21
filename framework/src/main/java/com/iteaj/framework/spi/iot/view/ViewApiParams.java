package com.iteaj.framework.spi.iot.view;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.consts.TimeCountType;
import com.iteaj.framework.spi.iot.data.TimeRangeResolver;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
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
     * 1. 支持具体的时间 如：2024-01-10 15:00,2024-01-16 15:00
     * 2. 支持now关键字 如：now-6,now
     * @see #timeType
     */
    private List<String> timeRange;

    /**
     * 值条件
     */
    private List<Value> values;

    /**
     * 范围值
     */
    private String rangeValue;

    /**
     * 产品轴类型(time, range, void)
     */
    private String category;

    /**
     * 范围类型(all、product、device、group...)
     */
    private String range;

    /**
     * 范围值(产品列表, 设备列表, 分组列表, ...)
     */
    private List<String> ranges;

    private TimeRangeResolver resolver;

    public void setRangeValue(String rangeValue) {
        this.rangeValue = rangeValue;
        if(StrUtil.isNotBlank(rangeValue)) {
            final String[] split = rangeValue.split(DataItemOption.SPLIT);
            if(split.length == 1) {
                this.range = split[0];
            } else if(split.length > 1) {
                this.range = split[0];
                this.ranges = Arrays.asList(split[1].split(","));
            } else {

            }
        }
    }

    public TimeRangeResolver getResolver() {
        if(resolver != null) {
            return resolver;
        }

        if(CollectionUtil.isNotEmpty(this.getTimeRange())) {
            return this.resolver = new TimeRangeResolver(this.getTimeType(), this.getTimeRange());
        }

        return resolver;
    }

    @Getter
    @Setter
    public static class Value {

        /**
         * 数据项
         */
        private String item;

        /**
         * 名
         */
        private String name;

        /**
         * 值
         */
        private String value;

        /**
         * 计算类型(count, min, max, avg, sum, ...)
         */
        private String calcType;

        private List<String> values;

        public Value build() {
            if(values != null) {
                return this;
            }

            if(StrUtil.isNotBlank(this.getValue())) {
                this.values = Arrays.asList(this.getValue().split(DataItemOption.SPLIT));
                this.calcType = this.values.get(this.values.size() - 1);
            } else {
                throw new ServiceException("项格式错误");
            }

            return this;
        }

        public String getValueItem(int index) {
            if(values == null) {
                build();
            }
            if(index > (this.values.size() - 1)) {
                return "";
            }

            return this.values.get(index);
        }
    }
}
