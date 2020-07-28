package com.iteaj.quartz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iteaj.quartz.entity.QrtzTriggers;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author iteaj
 * @since 2020-07-26
 */
public interface IQrtzTriggersService extends IService<QrtzTriggers> {

    IPage<QrtzTriggers> joinJobByPage(Page page, QrtzTriggers entity);
}
