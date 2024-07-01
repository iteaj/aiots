package com.iteaj.framework.spi.iot.consts;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 时间统计条件
 */
@Getter
@Setter
public class TimeCountCondition {

    /**
     * 计算类型
     */
    private CountCalcType calcType = CountCalcType.count;

    /**
     * 时间类型
     * @see #timeRange
     */
    private TimeCountType timeType;

    /**
     * 时间范围(格式 yyyy-MM-dd HH:mm)
     *
     * @see #timeType
     */
    private List<String> timeRange;

    /**
     * 业务类型
     */
//    private CountType countType;

    /**
     * 业务类型值
     */
    private List<String> value;
}
