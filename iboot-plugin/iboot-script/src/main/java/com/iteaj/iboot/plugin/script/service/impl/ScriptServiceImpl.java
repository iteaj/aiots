package com.iteaj.iboot.plugin.script.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.script.ScriptConfigService;
import com.iteaj.framework.spi.iot.listener.filter.FunctionScript;
import com.iteaj.framework.spi.iot.listener.filter.ScriptConfig;
import com.iteaj.iboot.plugin.script.entity.Script;
import com.iteaj.iboot.plugin.script.mapper.ScriptMapper;
import com.iteaj.iboot.plugin.script.service.IScriptService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 脚本引擎 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2025-04-04
 */
@Service
@CacheConfig(cacheNames = {ScriptServiceImpl.CACHE_KEY})
public class ScriptServiceImpl extends BaseServiceImpl<ScriptMapper, Script> implements IScriptService
        , ScriptConfigService, InitializingBean {

    private Cache cache;
    public static final String CACHE_KEY = "IBOOT:PLUGIN:CACHE:SCRIPT";

    public ScriptServiceImpl(CacheManager manager) {
        this.cache = manager.getCache(CACHE_KEY);
    }

    @Override
    public ListResult<Script> listByType(String type) {
        return this.list(Wrappers.<Script>lambdaQuery().eq(Script::getType, type));
    }

    @Override
    public FunctionScript getScript(String name) {
        final Cache.ValueWrapper valueWrapper = cache.get(name);
        if(valueWrapper != null) {
            return (FunctionScript) valueWrapper.get();
        }

        final Script data = this.getOne(Wrappers.<Script>lambdaQuery().eq(Script::getName, name)).getData();
        cache.put(name, data);
        return data;
    }

    @Override
    public BooleanResult save(Script entity) {
        return super.save(entity).ifPresentAndTrue(status -> cache.evict(entity.getName()));
    }

    @Override
    public BooleanResult updateById(Script entity) {
        return super.updateById(entity).ifPresentAndTrue(status -> cache.evict(entity.getName()));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ((ScriptConfigService.ScriptConfigServiceProxy) ScriptConfigService.instance).setProxy(this);
    }
}
