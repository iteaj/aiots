package com.iteaj.iboot.module.iot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.IVOption;
import com.iteaj.framework.result.*;
import com.iteaj.iboot.module.iot.entity.EventSource;
import com.iteaj.framework.IBaseService;

import java.util.List;

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

    ListResult<IVOption> availableSources(Long id);

    /**
     * 移除所有相关联的数据
     * @param idList
     * @return
     */
    BooleanResult removeAllByIds(List<Long> idList);
}
