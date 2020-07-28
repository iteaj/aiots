package com.iteaj.quartz.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iteaj.quartz.entity.QrtzTriggers;
import com.iteaj.quartz.mapper.IQrtzTriggersMapper;
import com.iteaj.quartz.service.IQrtzTriggersService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2020-07-26
 */
@Service
public class QrtzTriggersServiceImpl extends ServiceImpl<IQrtzTriggersMapper, QrtzTriggers> implements IQrtzTriggersService {

    @Override
    public IPage<QrtzTriggers> joinJobByPage(Page page, QrtzTriggers entity) {
        return getBaseMapper().joinJobByPage(page, entity);
    }
}
