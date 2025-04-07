package com.iteaj.iboot.plugin.open.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.ListResult;
import com.iteaj.iboot.plugin.open.entity.Northward;
import com.iteaj.iboot.plugin.open.mapper.NorthwardMapper;
import com.iteaj.iboot.plugin.open.service.INorthwardService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.iteaj.iboot.plugin.open.service.impl.NorthwardServiceImpl.CACHE_KEY;

/**
 * <p>
 * 北向接入 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2025-02-07
 */
@Service
@CacheConfig(cacheNames = {CACHE_KEY})
public class NorthwardServiceImpl extends BaseServiceImpl<NorthwardMapper, Northward> implements INorthwardService {

    private final CacheManager cacheManager;
    final static String CACHE_KEY = "IBOOT:CACHE:NORTHWARD";

    public NorthwardServiceImpl(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public List<Northward> listByType(String type) {
        Cache cache = cacheManager.getCache(CACHE_KEY);
        Cache.ValueWrapper valueWrapper = cache.get(type);
        if(valueWrapper != null) {
            return (List<Northward>) valueWrapper.get();
        }

        return this.list(Wrappers.<Northward>lambdaQuery().eq(Northward::getType, type))
                .ifPresent(item -> cache.put(type, item))
                .getData();
    }

    @Override
    public BooleanResult updateById(Northward entity) {
        return super.updateById(entity).ifPresentAndTrue(item -> {
            Cache cache = cacheManager.getCache(CACHE_KEY);
            cache.evict(entity.getType());
        });
    }
}
