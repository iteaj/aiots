package com.iteaj.iboot.module.iot.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.iboot.module.iot.entity.WarnConfig;
import com.iteaj.iboot.module.iot.mapper.WarnConfigMapper;
import com.iteaj.iboot.module.iot.service.IWarnConfigService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 告警配置 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2024-02-29
 */
@Service
public class WarnConfigServiceImpl extends BaseServiceImpl<WarnConfigMapper, WarnConfig> implements IWarnConfigService {

    @Override
    public BooleanResult save(WarnConfig entity) {
        this.getOne(Wrappers.<WarnConfig>lambdaQuery()
                .eq(WarnConfig::getProductId, entity.getProductId())
                .eq(WarnConfig::getName, entity.getName()))
                .ifPresentThrow("此产品已经包含告警["+entity.getName()+"]");
        return super.save(entity);
    }

    @Override
    public BooleanResult updateById(WarnConfig entity) {
        this.getOne(Wrappers.<WarnConfig>lambdaQuery()
                .ne(WarnConfig::getId, entity.getId())
                .eq(WarnConfig::getProductId, entity.getProductId())
                .eq(WarnConfig::getName, entity.getName()))
                .ifPresentThrow("此产品已经包含告警["+entity.getName()+"]");
        return super.updateById(entity);
    }
}
