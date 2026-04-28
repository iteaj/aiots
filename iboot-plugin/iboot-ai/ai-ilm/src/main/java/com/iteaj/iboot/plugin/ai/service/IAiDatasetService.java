package com.iteaj.iboot.plugin.ai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.IBaseService;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.iboot.plugin.ai.dto.AiDatasetDto;
import com.iteaj.iboot.plugin.ai.entity.AiDataset;

import java.io.Serializable;

/**
 * AI数据集服务接口
 * @author aiots-dev
 */
public interface IAiDatasetService extends IBaseService<AiDataset> {
    
    /**
     * 根据id查询数据集详情记录
     * @param id 主键id
     * @return 数据集详情记录
     */
    DetailResult<AiDatasetDto> detailById(Serializable id);

    /**
     * 分页查询数据集历史记录
     * @param page 分页参数
     * @param dto 查询条件
     * @return 分页结果
     */
    PageResult<IPage<AiDatasetDto>> pageOfDetail(Page<AiDataset> page, AiDatasetDto dto);
}
