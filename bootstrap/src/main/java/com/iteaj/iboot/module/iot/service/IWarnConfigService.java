package com.iteaj.iboot.module.iot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.iot.entity.WarnConfig;
import com.iteaj.framework.IBaseService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 告警配置 服务类
 * </p>
 *
 * @author iteaj
 * @since 2024-02-29
 */
public interface IWarnConfigService extends IBaseService<WarnConfig> {

    Map<Long, List<WarnConfig>> listCacheByProductId(Long productId);

    List<WarnConfig> listByProductIdAndModelAttrId(Long productId, Long modelAttrId);

    PageResult<IPage<WarnConfig>> pageDetail(Page<WarnConfig> page, WarnConfig entity);
}
