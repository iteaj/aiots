package com.iteaj.framework.result;

import com.iteaj.framework.Entity;

/**
 * create time: 2019/7/26
 *
 * @author iteaj
 * @since 1.0
 */
public class DetailResult<E extends Entity> extends OptionalResult<E, DetailResult<E>> {

    public DetailResult(E value) {
        super(value);
    }

    public DetailResult(E data, String message, long code) {
        super(data, message, code);
    }
}
