package com.iteaj.iboot.module.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.dto.CollectTaskDto;
import com.iteaj.iboot.module.iot.entity.CollectDetail;
import com.iteaj.iboot.module.iot.entity.CollectTask;

/**
 * <p>
 * 数据采集任务 Mapper 接口
 * </p>
 *
 * @author iteaj
 * @since 2022-08-28
 */
public interface CollectTaskMapper extends BaseMapper<CollectTask> {

    IPage<CollectTaskDto> detailOfPage(Page<CollectTaskDto> page, CollectTaskDto entity);

    CollectTaskDto detailById(Long id);

    CollectTaskDto collectDetailById(Long id);

    Page<CollectDetail> collectDetailPageById(Page page, Long id);
}
