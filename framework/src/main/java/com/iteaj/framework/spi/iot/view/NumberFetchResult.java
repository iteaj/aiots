package com.iteaj.framework.spi.iot.view;

public class NumberFetchResult extends ViewFetchResult {

    public NumberFetchResult(String item, Number value) {
        super(item, value);
    }

    @Override
    public Number getValue() {
        return (Number) super.getValue();
    }
}
