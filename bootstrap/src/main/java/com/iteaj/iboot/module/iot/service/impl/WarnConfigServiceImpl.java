package com.iteaj.iboot.module.iot.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.iboot.common.CacheKeys;
import com.iteaj.iboot.module.iot.entity.WarnConfig;
import com.iteaj.iboot.module.iot.mapper.WarnConfigMapper;
import com.iteaj.iboot.module.iot.service.IWarnConfigService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 告警配置 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2024-02-29
 */
@Service
@CacheConfig(cacheNames = {CacheKeys.IBOOT_CACHE_KEY_WARN_CONFIG})
public class WarnConfigServiceImpl extends BaseServiceImpl<WarnConfigMapper, WarnConfig> implements IWarnConfigService {

    private final Cache cache;

    public WarnConfigServiceImpl(CacheManager cacheManager) {
        this.cache = cacheManager.getCache(CacheKeys.IBOOT_CACHE_KEY_WARN_CONFIG);
    }

    @Override
    public BooleanResult save(WarnConfig entity) {
        this.getOne(Wrappers.<WarnConfig>lambdaQuery()
                .eq(WarnConfig::getProductId, entity.getProductId())
                .eq(WarnConfig::getName, entity.getName()))
                .ifPresentThrow("此产品已经包含告警["+entity.getName()+"]");
        return super.save(entity).ifPresent((status) -> cache.evict(entity.getProductId()));
    }

    @Override
    public BooleanResult updateById(WarnConfig entity) {
        this.getById(entity.getId()).ifPresent(config -> {
           if(!config.getProductId().equals(entity.getProductId())) {
               if(this.getBaseMapper().isBinded(entity.getId())) {
                   throw new ServiceException("已被场景联动绑定, 不允许修改产品");
               }
           }
        });

        this.getOne(Wrappers.<WarnConfig>lambdaQuery()
                .ne(WarnConfig::getId, entity.getId())
                .eq(WarnConfig::getProductId, entity.getProductId())
                .eq(WarnConfig::getName, entity.getName()))
                .ifPresentThrow("此产品已经包含告警["+entity.getName()+"]");
        return super.updateById(entity).ifPresent((status) -> cache.evict(entity.getProductId()));
    }

    @Override
    public Map<Long, List<WarnConfig>> listCacheByProductId(Long productId) {
        Cache.ValueWrapper valueWrapper = cache.get(productId);
        if(valueWrapper != null) {
            return (Map<Long, List<WarnConfig>>) valueWrapper.get();
        } else {
            Map<Long, List<WarnConfig>> result = new HashMap<>();
            List<WarnConfig> warnConfigs = this.list(Wrappers.<WarnConfig>lambdaQuery().eq(WarnConfig::getProductId, productId)).getData();
            if(warnConfigs != null) {
                result = warnConfigs.stream().collect(Collectors.groupingBy(WarnConfig::getModelAttrId));
            }

            cache.put(productId, result);
            return result;
        }
    }

    @Override
    public List<WarnConfig> listByProductIdAndModelAttrId(Long productId, Long modelAttrId) {
        Map<Long, List<WarnConfig>> longListMap = listCacheByProductId(productId);
        if (longListMap != null) {
            return longListMap.get(modelAttrId);
        }
        return List.of();
    }

    @Override
    public PageResult<IPage<WarnConfig>> pageDetail(Page<WarnConfig> page, WarnConfig entity) {
        this.getBaseMapper().pageDetail(page, entity);
        return new PageResult<>(page);
    }

    @Override
    public BooleanResult removeByIds(Collection<? extends Serializable> idList) {
        listByIds(idList).ifPresent(list -> {
            list.forEach(item -> cache.evict(item.getProductId()));
            super.removeByIds(idList);
        });
        return BooleanResult.buildTrue("删除成功");
    }
}
