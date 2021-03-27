package com.iteaj.oauth2.client;

import me.zhyd.oauth.cache.AuthStateCache;
import org.springframework.cache.Cache;

/**
 * create time: 2021/3/26
 *
 * @author iteaj
 * @since 1.0
 */
public class JustAuthStateCache implements AuthStateCache {

    private Cache cache;

    public JustAuthStateCache(Cache cache) {
        this.cache = cache;
    }

    public Cache getCache() {
        return cache;
    }

    public JustAuthStateCache setCache(Cache cache) {
        this.cache = cache;
        return this;
    }

    @Override
    public void cache(String key, String value) {
        this.cache.put(key, value);
    }

    @Override
    public void cache(String key, String value, long timeout) {
        this.cache.put(key, value);
    }

    @Override
    public String get(String key) {
        Cache.ValueWrapper wrapper = this.cache.get(key);
        if(wrapper != null) {
            return (String) wrapper.get();
        }

        return null;
    }

    @Override
    public boolean containsKey(String key) {
        return this.cache.get(key) != null;
    }
}
