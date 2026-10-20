package com.iteaj.iboot.module.iot.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.entity.WarnConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 告警配置 Mapper 接口
 * </p>
 *
 * @author iteaj
 * @since 2024-02-29
 */
public interface WarnConfigMapper extends BaseMapper<WarnConfig> {

    IPage<WarnConfig> pageDetail(Page<WarnConfig> page, WarnConfig entity);

    boolean isBinded(Long id);
}
