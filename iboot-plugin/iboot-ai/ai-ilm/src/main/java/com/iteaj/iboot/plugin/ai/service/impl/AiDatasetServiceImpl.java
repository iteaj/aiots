package com.iteaj.iboot.plugin.ai.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.iboot.plugin.ai.dto.AiDatasetDto;
import com.iteaj.iboot.plugin.ai.entity.AiDataset;
import com.iteaj.iboot.plugin.ai.mapper.AiDatasetMapper;
import com.iteaj.iboot.plugin.ai.service.IAiDatasetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * AI数据集服务实现
 * @author aiots-dev
 */
@Slf4j
@Service
public class AiDatasetServiceImpl extends BaseServiceImpl<AiDatasetMapper, AiDataset> implements IAiDatasetService {

    /**
     * 根据id查询数据集详情记录
     * @param id 主键id
     * @return 数据集详情记录
     */
    @Override
    public DetailResult<AiDatasetDto> detailById(Serializable id) {
        return new DetailResult<>(this.getBaseMapper().detailById(id));
    }

    /**
     * 根据条件查询数据集列表分页详情记录
     * @param page 分页参数
     * @param dto 查询条件
     * @return 数据集分页记录
     */
    @Override
    public PageResult<IPage<AiDatasetDto>> pageOfDetail(Page<AiDataset> page, AiDatasetDto dto) {
        return new PageResult<>(this.getBaseMapper().pageOfDetail(page, dto));
    }
}
