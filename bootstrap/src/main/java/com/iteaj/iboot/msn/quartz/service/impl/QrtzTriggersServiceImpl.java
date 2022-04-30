package com.iteaj.iboot.msn.quartz.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.iboot.msn.quartz.entity.QrtzTriggers;
import com.iteaj.iboot.msn.quartz.mapper.IQrtzTriggersMapper;
import com.iteaj.iboot.msn.quartz.service.IQrtzTriggersService;
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
public class QrtzTriggersServiceImpl extends BaseServiceImpl<IQrtzTriggersMapper, QrtzTriggers> implements IQrtzTriggersService {

    @Override
    public QrtzTriggers getDetail(String schedName, String jobName) {
        return getBaseMapper().getDetail(schedName, jobName);
    }

    @Override
    public IPage<QrtzTriggers> joinJobByPage(Page page, QrtzTriggers entity) {
        return getBaseMapper().joinJobByPage(page, entity);
    }
}
