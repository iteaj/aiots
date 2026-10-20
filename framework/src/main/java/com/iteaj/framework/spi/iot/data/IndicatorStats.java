package com.iteaj.framework.spi.iot.data;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 数据指标统计结果
 */
@Data
@Accessors(chain = true)
public class IndicatorStats {

    /** 属性标识符 */
    private String field;

    /** 属性ID */
    private Long fieldId;

    /** 最大值 */
    private Double max;

    /** 最大值发生时间 */
    private Date maxTime;

    /** 最小值 */
    private Double min;

    /** 最小值发生时间 */
    private Date minTime;

    /** 平均值 */
    private Double avg;

    /** 当前值（最新值） */
    private Double current;

    /** 数据量 */
    private Long count;
}
