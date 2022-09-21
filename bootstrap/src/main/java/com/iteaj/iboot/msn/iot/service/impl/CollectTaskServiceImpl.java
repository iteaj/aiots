package com.iteaj.iboot.msn.iot.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.msn.iot.dto.CollectTaskDto;
import com.iteaj.iboot.msn.iot.entity.CollectTask;
import com.iteaj.iboot.msn.iot.mapper.CollectTaskMapper;
import com.iteaj.iboot.msn.iot.service.ICollectTaskService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 数据采集任务 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2022-08-28
 */
@Service
public class CollectTaskServiceImpl extends BaseServiceImpl<CollectTaskMapper, CollectTask> implements ICollectTaskService {

    @Override
    public Result<IPage<CollectTaskDto>> detailOfPage(Page<CollectTaskDto> page, CollectTaskDto entity) {
        return new PageResult<>(this.getBaseMapper().detailOfPage(page, entity));
    }

    @Override
    public DetailResult<CollectTaskDto> detailById(Long id) {
        return new DetailResult<>(getBaseMapper().detailById(id));
    }

    @Override
    public CollectTaskDto signalsDetailById(Long id) {
        return getBaseMapper().signalsDetailById(id);
    }
}
