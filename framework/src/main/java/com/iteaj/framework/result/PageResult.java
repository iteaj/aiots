package com.iteaj.framework.result;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.beans.Transient;
import java.util.Collections;
import java.util.stream.Stream;

/**
 * create time: 2019/7/26
 *
 * @author iteaj
 * @since 1.0
 */
public class PageResult<E extends IPage> extends OptionalResult<E, PageResult<E>> {

    public PageResult(E value) {
        super(value);
    }

    public PageResult(E data, String message, long code) {
        super(data, message, code);
    }

    @Transient
    public long getSize() {
        return getData().getSize();
    }

    @Transient
    public long getCurrent() {
        return getData().getCurrent();
    }

    @Transient
    public long getTotal() {
        return getData().getTotal();
    }

    /**
     * 返回记录数列表的stream
     * @return
     */
    public Stream<E> stream() {
        return ofNullable().map(eiPage -> eiPage
                .getRecords()).orElse(Collections.EMPTY_LIST).stream();
    }
}
