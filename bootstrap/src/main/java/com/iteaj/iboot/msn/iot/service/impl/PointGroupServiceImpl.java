package com.iteaj.iboot.msn.iot.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.msn.iot.entity.PointGroup;
import com.iteaj.iboot.msn.iot.mapper.PointGroupMapper;
import com.iteaj.iboot.msn.iot.service.IPointGroupService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 点位组 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2022-07-22
 */
@Service
public class PointGroupServiceImpl extends BaseServiceImpl<PointGroupMapper, PointGroup> implements IPointGroupService {

    @Override
    public PageResult<IPage<PointGroup>> detailOfPage(Page<PointGroup> page, PointGroup entity) {
        return new PageResult<IPage<PointGroup>>(this.getBaseMapper().detailOfPage(page, entity));
    }

    @Override
    public DetailResult<PointGroup> detailById(Long id) {
        return new DetailResult<>(this.getBaseMapper().detailById(id));
    }
}
