package com.iteaj.framework.api;

import java.io.Serializable;
import java.util.List;

public interface ModuleManager {

    /**
     * 是否存在模块
     * @param msn
     * @return
     */
    boolean exists(Serializable msn);

    /**
     * 注册模块
     * @param module
     */
    void register(Module module);

    /**
     * 获取当前所有模块
     * @return
     */
    List<Module> getModules();

    /**
     * 获取指定模块
     * @param msn
     * @return
     */
    Module getModule(Serializable msn);
}
