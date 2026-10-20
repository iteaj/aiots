package com.iteaj.iboot.module.core.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.iboot.common.CacheKeys;
import com.iteaj.iboot.module.core.enums.ConfigType;
import com.iteaj.iboot.module.core.mapper.IConfigDao;
import com.iteaj.iboot.module.core.entity.Config;
import com.iteaj.iboot.module.core.service.IConfigService;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;

/**
 * create time: 2019/12/4
 *
 * @author iteaj
 * @since 1.0
 */
@Service
@CacheConfig(cacheNames = {CacheKeys.IBOOT_CACHE_CONFIG})
public class ConfigServiceImpl extends BaseServiceImpl<IConfigDao, Config> implements IConfigService {

    private final Cache cache;

    public ConfigServiceImpl(CacheManager cacheManager) {
        this.cache = cacheManager.getCache(CacheKeys.IBOOT_CACHE_CONFIG);
    }

    @Override
    public BooleanResult save(Config entity) {
        getOne(Wrappers.<Config>lambdaQuery().eq(Config::getLabel, entity.getLabel())).ofNullable()
                .ifPresent(item -> {throw new ServiceException("配置标识重复["+entity.getLabel()+"]");});

        getOne(Wrappers.<Config>lambdaQuery().eq(Config::getName, entity.getName())).ofNullable()
                .ifPresent(item -> {throw new ServiceException("配置名称重复["+entity.getName()+"]");});

        return super.save(entity);
    }

    @Override
    @CacheEvict(key = "'ByLabel:'+#entity.label")
    public BooleanResult updateById(Config entity) {
        entity.setType(null); // 配置类型不允许更新
        getOne(Wrappers.<Config>lambdaQuery().eq(Config::getName, entity.getName())).ofNullable()
                .ifPresent(item -> {
                    // 名称重复
                    if(entity.getId().compareTo(item.getId()) != 0) {
                        throw new ServiceException("配置名称重复["+entity.getName()+"]");
                    }
                });
        return super.updateById(entity);
    }

    @Override
    public BooleanResult removeByIds(Collection<? extends Serializable> idList) {
        if(CollectionUtil.isEmpty(idList)) {
            return BooleanResult.buildFalse("未指定要删除的记录");
        }

        listByIds(idList).forEach(item -> {
            if(item != null && item.getType() == ConfigType.sys) {
                throw new ServiceException("系统配置不允许删除");
            }
        }).forEach(item -> {
            if(item != null) {
                cache.evict("ByLabel:" + item.getLabel());
            }
        });
        return super.removeByIds(idList);
    }

    @Override
    @Cacheable(key = "'ByLabel:'+#label")
    public Config getConfigByLabel(String label) {
        return this.getOne(Wrappers.<Config>lambdaQuery().eq(Config::getLabel, label)).getData();
    }
}
