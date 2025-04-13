package com.iteaj.framework.spi.iot.listener.filter;

import java.util.List;

/**
 * 功能脚本
 */
public interface FunctionScript {

    /**
     * 功能类型
     * @return
     */
    String getType();

    /**
     * 功能名称
     * @return
     */
    String getName();

    /**
     * 选中的语言
     * @return
     */
    String getLanguage();

    /**
     * 脚本实现
     * @return
     */
    List<ScriptConfig> getConfigs();

}
