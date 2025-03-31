package com.iteaj.iboot.plugin.open.service;

import com.iteaj.iboot.plugin.open.entity.Northward;
import com.iteaj.framework.IBaseService;

import java.util.List;

/**
 * <p>
 * 北向接入 服务类
 * </p>
 *
 * @author iteaj
 * @since 2025-02-07
 */
public interface INorthwardService extends IBaseService<Northward> {

    List<Northward> listByType(String type);
}
