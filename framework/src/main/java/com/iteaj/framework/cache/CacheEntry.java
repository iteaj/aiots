package com.iteaj.framework.cache;

/**
 * create time: 2021/3/21
 *
 * @author iteaj
 * @since 1.0
 */
public class CacheEntry<C> {

    private C value;
    private String name;

    public CacheEntry(String name, C value) {
        this.value = value;
        this.name = name;
    }

    public C getValue() {
        return value;
    }

    public CacheEntry<C> setValue(C value) {
        this.value = value;
        return this;
    }

    public String getName() {
        return name;
    }

    public CacheEntry<C> setName(String name) {
        this.name = name;
        return this;
    }
}
