package com.iteaj.framework.spi.iot.data;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RealtimeDataExtra {

    /**
     * 用于大于等于时间间隔的检查
     */
    private long geStartTime;

    /**
     * 用于联动脚本大于等于时间间隔的检查
     */
    private long linkageGeStartTime;
}
