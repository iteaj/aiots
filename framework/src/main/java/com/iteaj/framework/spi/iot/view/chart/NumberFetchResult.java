package com.iteaj.framework.spi.iot.view.chart;

public class NumberFetchResult extends ChartFetchResult {

    public NumberFetchResult(String item, Number value) {
        super(item, value);
    }

    @Override
    public Number getValue() {
        return (Number) super.getValue();
    }
}
