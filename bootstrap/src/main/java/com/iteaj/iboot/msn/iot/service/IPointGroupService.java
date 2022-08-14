package com.iteaj.iboot.msn.iot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.msn.iot.entity.PointGroup;
import com.iteaj.framework.IBaseService;

/**
 * <p>
 * 点位组 服务类
 * </p>
 *
 * @author iteaj
 * @since 2022-07-22
 */
public interface IPointGroupService extends IBaseService<PointGroup> {

    /**
     * 返回页面详情信息
     * @param page
     * @param entity
     * @return
     */
    PageResult<IPage<PointGroup>> detailOfPage(Page<PointGroup> page, PointGroup entity);

    /**
     * 获取详情信息
     * @param id
     * @return
     */
    DetailResult<PointGroup> detailById(Long id);
}
