package com.iteaj.iboot.module.iot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.iot.entity.EventSource;
import com.iteaj.framework.IBaseService;

/**
 * <p>
 * 事件源 服务类
 * </p>
 *
 * @author iteaj
 * @since 2024-02-27
 */
public interface IEventSourceService extends IBaseService<EventSource> {

    PageResult<IPage<EventSource>> detailPage(Page<EventSource> page, EventSource entity);

    DetailResult<EventSource> detailById(Long id);

    BooleanResult updateConfig(EventSource entity);
}
