package com.iteaj.framework.result;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * create time: 2019/7/26
 *
 * @author iteaj
 * @since 1.0
 */
public class ListResult<E> extends OptionalResult<List<E>, ListResult<E>> {

    public ListResult(List<E> value) {
        super(value);
    }

    public ListResult(List<E> data, String message, long code) {
        super(data, message, code);
    }

    public int size() {
        return getData().size();
    }

    public Iterator iterable() {
        return getData() == null ? Collections.emptyIterator() : getData().iterator();
    }

    public boolean isEmpty() {
        return getData() == null ? true : getData().isEmpty();
    }

    public void forEach(Consumer<E> consumer) {
        this.stream().forEach(consumer);
    }

    public Stream<E> stream() {
        return getData() == null ? Stream.empty() : getData().stream();
    }
}
