package com.iteaj.iboot.module.core.service;

import com.iteaj.framework.IBaseService;
import com.iteaj.iboot.module.core.entity.Config;

/**
 * create time: 2019/12/4
 *
 * @author iteaj
 * @since 1.0
 */
public interface IConfigService extends IBaseService<Config> {

    /**
     * 获取指定标签配置
     * @param label 标签值
     * @return 系统配置 {@link Config}
     */
    Config getConfigByLabel(String label);
}
