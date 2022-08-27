package com.iteaj.iboot.msn.iot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.msn.iot.entity.Signal;
import com.iteaj.framework.IBaseService;

/**
 * <p>
 * 寄存器点位 服务类
 * </p>
 *
 * @author iteaj
 * @since 2022-07-22
 */
public interface ISignalService extends IBaseService<Signal> {

    Result<IPage<Signal>> detailByPage(Page<Signal> page, Signal entity);
}
