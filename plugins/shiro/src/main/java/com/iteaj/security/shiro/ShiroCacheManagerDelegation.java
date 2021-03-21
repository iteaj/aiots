package com.iteaj.security.shiro;

import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * create time: 2020/6/20
 * @see org.springframework.cache.CacheManager 默认使用spring 缓存机制
 * @author iteaj
 * @since 1.0
 */
public class ShiroCacheManagerDelegation extends AbstractCacheManager {

    private org.springframework.cache.CacheManager cacheManager;

    public ShiroCacheManagerDelegation(org.springframework.cache.CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
        Cache<Object, Object> objectCache = super.getCache(s);
        if(objectCache != null) {
            return (Cache<K, V>) objectCache;
        }

        return null;
    }

    @Override
    protected Cache createCache(String s) throws CacheException {
        org.springframework.cache.Cache cache = cacheManager.getCache(s);
        if(cache != null) {
            return new CacheDelegation(cache);
        }

        return null;
    }

    protected class CacheDelegation implements Cache {

        private org.springframework.cache.Cache delegation;

        public CacheDelegation(org.springframework.cache.Cache delegation) {
            this.delegation = delegation;
        }

        @Override
        public Object get(Object o) throws CacheException {
            org.springframework.cache.Cache.ValueWrapper valueWrapper = this.delegation.get(o);
            if(valueWrapper == null) {
                return null;
            } else {
                return valueWrapper.get();
            }
        }

        @Override
        public Object put(Object o, Object o2) throws CacheException {
            this.delegation.put(o, o2);
            return o2;
        }

        @Override
        public Object remove(Object o) throws CacheException {
            Object o1 = this.get(o);
            if(o1 != null) {
                this.delegation.evict(o);
            }
            return o1;
        }

        @Override
        public void clear() throws CacheException {
            this.delegation.clear();
        }

        @Override
        public int size() {
            Object nativeCache = this.delegation.getNativeCache();
            if(nativeCache instanceof Map) {
                return ((Map) nativeCache).size();
            }

            throw new IllegalStateException("未适配的缓存对象");
        }

        @Override
        public Set keys() {
            Object nativeCache = this.delegation.getNativeCache();
            if(nativeCache instanceof Map) {
                return ((Map) nativeCache).keySet();
            } else {
                throw new IllegalStateException("未适配的缓存对象");
            }
        }

        @Override
        public Collection values() {
            Object nativeCache = this.delegation.getNativeCache();
            if(nativeCache instanceof Map) {
                return ((Map) nativeCache).values();
            } else {
                throw new IllegalStateException("未适配的缓存对象");
            }
        }
    }
}
