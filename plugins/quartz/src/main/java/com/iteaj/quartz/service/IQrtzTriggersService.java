package com.iteaj.quartz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.IBaseService;
import com.iteaj.quartz.entity.QrtzTriggers;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author iteaj
 * @since 2020-07-26
 */
public interface IQrtzTriggersService extends IBaseService<QrtzTriggers> {

    QrtzTriggers getDetail(String schedName, String jobName);

    IPage<QrtzTriggers> joinJobByPage(Page page, QrtzTriggers entity);
}
