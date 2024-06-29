package com.iteaj.framework.spi.iot.consts;

import com.iteaj.framework.spi.iot.consts.CountType;
import com.iteaj.framework.spi.iot.consts.TimeCountType;
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
     * 统计类型
     */
    private CountType type;

    /**
     * 计算类型
     */
    private CountCalcType calcType;

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
    private String serviceType;

    /**
     * 待统计的属性列表
     * -1标识所有
     */
    private List<String> attrs;
}
