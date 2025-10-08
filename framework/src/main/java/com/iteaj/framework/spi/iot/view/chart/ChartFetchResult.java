package com.iteaj.framework.spi.iot.view.chart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ChartFetchResult {

    /**
     * 项
     * @see ChartApiParams.Value#getItem()
     */
    private String item;

    /**
     * 值
     */
    private Object value;

    public ChartFetchResult(String item, Object value) {
        this.item = item;
        this.value = value;
    }
}
