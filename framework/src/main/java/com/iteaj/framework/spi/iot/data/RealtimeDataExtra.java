package com.iteaj.framework.spi.iot.data;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RealtimeDataExtra {

    /**
     * 等级
     */
    private String grade;

    /**
     * 指标
     */
    private String indicator;
}
