package com.iteaj.iboot.plugin.script.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.script.ScriptConfigService;
import com.iteaj.framework.script.FunctionScript;
import com.iteaj.iboot.plugin.script.entity.Script;
import com.iteaj.iboot.plugin.script.mapper.ScriptMapper;
import com.iteaj.iboot.plugin.script.service.IScriptService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    public Boolean isJoinModelApi(String name) {
        return this.getBaseMapper().isJoinModelApi(name);
    }

    @Override
    public Boolean isJoinModelAttr(String key) {
        return this.getBaseMapper().isJoinModelAttr(key);
    }

    @Override
    public List<FunctionScript> getScriptByType(String type) {
        final Cache.ValueWrapper valueWrapper = cache.get(type);
        if(valueWrapper != null) {
            return (List<FunctionScript>) valueWrapper.get();
        }

        final List<FunctionScript> scripts = this.listByType(type).stream()
                .map(item -> (FunctionScript) item).collect(Collectors.toList());
        cache.put(type, scripts);
        return scripts;
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
        if(StrUtil.isBlank(entity.getName())) {
            return BooleanResult.buildFalse("脚本名字必填");
        }
        this.getOne(Wrappers.<Script>lambdaQuery()
                .eq(Script::getName, entity.getName()))
                .ifPresentThrow("此脚本已经存在["+entity.getName()+"]");
        return super.save(entity).ifPresentAndTrue(status -> {
            cache.evict(entity.getName());
            cache.evict(entity.getType());
        });
    }

    @Override
    public BooleanResult updateById(Script entity) {
        this.getOne(Wrappers.<Script>lambdaQuery()
                .eq(Script::getName, entity.getName())
                .ne(Script::getId, entity.getId()))
                .ifPresentThrow("此脚本已经存在["+entity.getName()+"]");
        return super.updateById(entity).ifPresentAndTrue(status -> {
            cache.evict(entity.getName());
            cache.evict(entity.getType());
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ((ScriptConfigService.ScriptConfigServiceProxy) ScriptConfigService.instance).setProxy(this);
    }
}
