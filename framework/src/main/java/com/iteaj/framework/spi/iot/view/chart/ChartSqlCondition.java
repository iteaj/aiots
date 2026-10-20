package com.iteaj.framework.spi.iot.view.chart;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.data.BaseEchartsCount;
import com.iteaj.framework.spi.iot.data.TimeRangeResolver;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.*;

@Getter
@Setter
@Accessors(chain = true)
public class ChartSqlCondition {

    private String productCode;

    private String field;

    private String calcType;

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

    private String timeType;

    private TimeRangeResolver resolver;

    public ChartSqlCondition(String productCode, String field, String calcType) {
        this.productCode = productCode;
        this.field = field;
        this.calcType = calcType;
    }

    public static ChartSqlCondition build(String productCode, String field, String calcType, ChartApiParams params) {
        List<String> ranges = params.getRanges();
        if(Objects.equals("range", params.getCategory())){
            if(Objects.equals("all", params.getRange())) {
                throw new ServiceException("按范围的产品轴不能选用所有范围");
            } else if(CollectionUtil.isEmpty(ranges)) {
                throw new ServiceException("按范围必须选择范围配置");
            }
        }

        return new ChartSqlCondition(productCode, field, calcType).setRanges(ranges)
                .setCategory(params.getCategory()).setTimeType(params.getTimeType().name())
                .setRange(params.getRange()).setResolver(params.getResolver());
    }

    public String getStartTime() {
        return resolver.getStartTime();
    }

    public String getEndTime() {
        return resolver.getEndTime();
    }

    public List<BaseEchartsCount> buildTimeValues(List<BaseEchartsCount> counts) {
        return this.resolver.buildValues(counts);
    }
}
