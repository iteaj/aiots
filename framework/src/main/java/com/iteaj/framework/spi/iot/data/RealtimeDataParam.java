package com.iteaj.framework.spi.iot.data;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RealtimeDataParam extends DefaultRealtimeData {

    private String startTime;

    private String endTime;

    private Long productId;

    private Long startTimestamp;

    private Long endTimestamp;

    public RealtimeDataParam setStartTime(String startTime) {
        this.startTime = startTime;
        if(StrUtil.isNotEmpty(startTime)){
            startTimestamp = DateUtil.parse(startTime, DatePattern.NORM_DATETIME_FORMATTER).getTime();
        }

        return this;
    }

    public RealtimeDataParam setEndTime(String endTime) {
        this.endTime = endTime;
        if(StrUtil.isNotEmpty(endTime)){
            endTimestamp = DateUtil.parse(endTime, DatePattern.NORM_DATETIME_FORMATTER).getTime();
        }

        return this;
    }
}
