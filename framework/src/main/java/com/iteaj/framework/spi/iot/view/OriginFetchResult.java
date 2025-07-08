package com.iteaj.framework.spi.iot.view;

import lombok.Getter;

/**
 * 自定义对象获取
 */
@Getter
public abstract class OriginFetchResult extends ViewFetchResult{

    /**
     * 是否是原始数据
     * 原始数据全段不做处理, 直接渲染
     */
    private boolean origin;

    public OriginFetchResult(Object value) {
        super(null, value);
        this.origin = true;
    }
}
