package com.iteaj.framework.api.module;

import com.iteaj.framework.api.Module;
import com.iteaj.framework.api.ModuleManager;
import org.apache.commons.collections.MapUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LocalModuleManager implements ModuleManager {

    private Map<Serializable, Module> moduleMap = new ConcurrentHashMap();

    @Override
    public boolean exists(Serializable msn) {
        return moduleMap.containsKey(msn);
    }

    @Override
    public void register(Module module) {
        if(null == module) throw new ModuleException("不存在要注册的模块实例");

        if(module.getMsn() == null || StringUtils.isEmpty(module.getName()))
            throw new ModuleException("要注册的模块信息无效或不存在");

        if(exists(module.getMsn()))
            throw new ModuleException("此模块已经注册");

        moduleMap.put(module.getMsn(), module);
    }

    @Override
    public List<Module> getModules() {
        return moduleMap.values().stream()
                .collect(Collectors.toList());
    }

    @Override
    public Module getModule(Serializable msn) {
        return moduleMap.get(msn);
    }
}
