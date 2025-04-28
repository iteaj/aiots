package com.iteaj.iboot.plugin.script.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.script.*;
import com.iteaj.iboot.plugin.script.entity.BindDto;
import com.iteaj.iboot.plugin.script.entity.Script;
import com.iteaj.iboot.plugin.script.mapper.ScriptMapper;
import com.iteaj.iboot.plugin.script.model.LinkageScriptRealtimeListener;
import com.iteaj.iboot.plugin.script.service.IScriptService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 脚本引擎 服务实现类
 * </p>
 * todo 集群编译缓存方案
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
    public ListResult<BindDto> bindApis(Long id) {
        final Script script = this.getById(id).ifNotPresentThrow("记录不存在").getData();
        return new ListResult<>(this.getBaseMapper().bindApis(script.getKey()));
    }

    @Override
    public ListResult<BindDto> bindAttrs(Long id) {
        final Script script = this.getById(id).ifNotPresentThrow("记录不存在").getData();
        return new ListResult<>(this.getBaseMapper().bindAttrs(script.getKey()));
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
    public FunctionScript getScript(String key) {
        final Cache.ValueWrapper valueWrapper = cache.get(key);
        if(valueWrapper != null) {
            return (FunctionScript) valueWrapper.get();
        }

        Script data;
        final boolean starts = key.startsWith(Script.KEY_PREFIX);
        if(starts) {
            data = this.getById(key.split(":")[1]).getData();
        } else {
            data = this.getOne(Wrappers.<Script>lambdaQuery().eq(Script::getName, key)).getData();
        }

        if(data != null) {
            final ScriptConfig scriptConfig = getScriptConfig(data);
            if(scriptConfig != null) {
                // 进行脚本编译并注册
                ScriptManager.register(key, scriptConfig);
            } else {
                data = null; // 没有选中使用的语言相当于禁用
            }
        }

        cache.put(key, data);
        return data;
    }

    @Override
    @Transactional
    public BooleanResult save(Script entity) {
        if(StrUtil.isBlank(entity.getName())) {
            return BooleanResult.buildFalse("脚本名字必填");
        }

        this.getOne(Wrappers.<Script>lambdaQuery()
                .eq(Script::getType, entity.getType())
                .eq(Script::getName, entity.getName()))
                .ifPresentThrow("此脚本已经存在["+entity.getName()+"]");

        entity.setUpdateTime(new Date());
        return super.save(entity).ifPresentAndTrue(status -> {
            removeCache(entity);
            registerCompilable(entity);
        });
    }

    private void removeCache(Script entity) {
        cache.evict(entity.getKey());
        cache.evict(entity.getType());
        if(entity.getType().equals(Script.TYPE_FILTER)) {
            cache.evict(entity.getName());
        }
    }

    private void registerCompilable(Script entity) {
        final ScriptConfig scriptConfig = this.getScriptConfig(entity);
        if (scriptConfig != null) { // 有选中语言
            try {
                if(entity.getType().equals(Script.TYPE_FILTER)) {
                    ScriptManager.register(entity.getName(), scriptConfig);
                } else {
                    if(entity.getId() == null) {
                        throw new ServiceException("创建脚本Key失败");
                    }

                    ScriptManager.register(entity.getKey(), scriptConfig);
                    if(entity.getType().equals(Script.TYPE_LINKAGE)) {
                        LinkageScriptRealtimeListener.register(entity.getKey(), scriptConfig);
                    }
                }
            } catch (ScriptExecException e) {
                throw new ServiceException(e.getMessage(), e);
            }
        } else { // 禁用
            if(entity.getType().equals(Script.TYPE_FILTER)) {
                ScriptManager.removeCompilable(entity.getName());
            } else {
                ScriptManager.removeCompilable(entity.getKey());
                if(entity.getType().equals(Script.TYPE_LINKAGE)) {
                    LinkageScriptRealtimeListener.remove(entity.getKey());
                }
            }
        }
    }

    @Override
    @Transactional
    public BooleanResult updateById(Script entity) {
        this.getOne(Wrappers.<Script>lambdaQuery()
                .eq(Script::getType, entity.getType())
                .eq(Script::getName, entity.getName())
                .ne(Script::getId, entity.getId()))
                .ifPresentThrow("此脚本已经存在["+entity.getName()+"]");

        entity.setUpdateTime(new Date());
        return super.updateById(entity).ifPresentAndTrue(status -> {
            removeCache(entity);
            registerCompilable(entity);
        });
    }

    @Override
    @Transactional
    public BooleanResult removeById(Serializable id) {
        final Script script = this.getById(id).ifNotPresentThrow("记录不存在").getData();
        return super.removeById(id).ifPresentAndTrue((status) -> {
            removeCache(script);
            ScriptManager.removeCompilable(script.getKey());
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ((ScriptConfigService.ScriptConfigServiceProxy) ScriptConfigService.instance).setProxy(this);
    }
}
