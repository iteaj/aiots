package com.iteaj.iboot.module.iot.collect.model;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iteaj.framework.spi.iot.BatchOfPoint;
import com.iteaj.framework.spi.iot.data.ModelAttrMeta;
import com.iteaj.framework.spi.iot.data.ModelAttrMetaService;
import com.iteaj.framework.spi.iot.listener.EntityPayload;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.StatusSwitchListener;
import com.iteaj.iboot.common.CacheKeys;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.dto.ProductDto;
import com.iteaj.iboot.module.iot.entity.Product;
import com.iteaj.iboot.module.iot.mapper.ProductMapper;
import com.iteaj.iboot.module.iot.service.IProductService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

@Component
@CacheConfig(cacheNames = {CacheKeys.IBOOT_CACHE_KEY_POINTS, CacheKeys.IBOOT_CACHE_KEY_PRODUCT_MODEL})
public class BatchProductManagerListener implements StatusSwitchListener {

    private static Cache cache;
    public final IProductService productService;
    private static ModelAttrMetaService modelAttrMetaService;
    public static final Map<String, List<List<BatchOfPoint>>> NOT_BATCH_POINTS = new HashMap<>();

    public BatchProductManagerListener(CacheManager cacheManager, IProductService productService, ModelAttrMetaService modelAttrMetaService) {
        cache = cacheManager.getCache(CacheKeys.IBOOT_CACHE_KEY_POINTS);
        this.productService = productService;
        BatchProductManagerListener.modelAttrMetaService = modelAttrMetaService;
    }

    @Override
    public boolean isEventMatcher(IotEventType eventType) {
        return eventType == IotEventType.ProductSwitch;
    }

    @Override
    public void onIotEvent(IotEventType eventType, EntityPayload payload) {
        ProductDto productDto = (ProductDto) payload.getPayload();
        ProductMapper productMapper = (ProductMapper) productService.getBaseMapper();
        final FuncStatus status = productDto.getStatus();
        if(status == FuncStatus.disabled) {
            // 移除缓存
            cache.evict(productDto.getId());
            modelAttrMetaService.removeAttrMetas(productDto.getCode());
        } else {
            // 缓存
            modelAttrMetaService.getAttrMetas(productDto.getCode());
        }
    }

    public static Collection<ModelAttrMeta> getAttrMetas(Serializable productCode) {
        Map<Serializable, ModelAttrMeta> attrMetas = modelAttrMetaService.getAttrMetas(productCode);
        if(attrMetas != null) {
            return attrMetas.values();
        }

        return Collections.emptyList();
    }

    public static Map<String, List<List<BatchOfPoint>>> getPoints(Long productId) {
        Cache.ValueWrapper valueWrapper = cache.get(productId);
        return valueWrapper != null ? (Map<String, List<List<BatchOfPoint>>>) valueWrapper.get() : null;
    }

    public static synchronized void register(Long productId, Map<String, List<List<BatchOfPoint>>> points) {
        cache.put(productId, points);
    }
}
