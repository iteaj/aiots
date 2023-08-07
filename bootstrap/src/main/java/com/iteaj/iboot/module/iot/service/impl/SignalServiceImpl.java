package com.iteaj.iboot.module.iot.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.iot.entity.Signal;
import com.iteaj.iboot.module.iot.mapper.SignalMapper;
import com.iteaj.iboot.module.iot.service.IPointGroupService;
import com.iteaj.iboot.module.iot.service.ISignalService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;

/**
 * <p>
 * 寄存器点位 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2022-07-22
 */
@Service
public class SignalServiceImpl extends BaseServiceImpl<SignalMapper, Signal> implements ISignalService {

    @Autowired
    private IPointGroupService pointGroupService;

    @Override
    public Result<IPage<Signal>> detailByPage(Page<Signal> page, Signal entity) {
        return new PageResult<>(getBaseMapper().detailByPage(page, entity));
    }

    @Override
    public BooleanResult removeByIds(Collection<? extends Serializable> idList) {
        if(CollectionUtils.isEmpty(idList)) {
            return new BooleanResult(false);
        }

        if(pointGroupService.isBindSignals(idList)) {
            throw new ServiceException("此点位已被点位组使用");
        }

        return super.removeByIds(idList);
    }
}
