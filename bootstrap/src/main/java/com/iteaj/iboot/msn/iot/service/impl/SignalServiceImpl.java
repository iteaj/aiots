package com.iteaj.iboot.msn.iot.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.msn.iot.mapper.SignalMapper;
import com.iteaj.iboot.msn.iot.service.ISignalService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

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

    @Override
    public Result<IPage<Signal>> detailByPage(Page<Signal> page, Signal entity) {
        return new PageResult<>(getBaseMapper().detailByPage(page, entity));
    }
}
