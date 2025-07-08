package com.iteaj.framework.spi.iot.view;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ViewFetchResult {

    /**
     * 项
     * @see ViewApiParams.Value#getItem()
     */
    private String item;

    /**
     * 值
     */
    private Object value;

    public ViewFetchResult(String item, Object value) {
        this.item = item;
        this.value = value;
    }
}
