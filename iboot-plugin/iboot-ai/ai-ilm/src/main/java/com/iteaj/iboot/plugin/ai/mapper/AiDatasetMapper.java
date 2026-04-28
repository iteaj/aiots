package com.iteaj.iboot.plugin.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.iboot.plugin.ai.dto.AiDatasetDto;
import com.iteaj.iboot.plugin.ai.entity.AiDataset;
import org.apache.ibatis.annotations.Mapper;

import java.io.Serializable;

/**
 * AI数据集Mapper接口
 * @author aiots-dev
 */
@Mapper
public interface AiDatasetMapper extends BaseMapper<AiDataset> {

    /**
     * 根据ID查询数据集详情（含明细列表）
     * @param id 主键ID
     * @return 数据集详情DTO
     */
    AiDatasetDto detailById(Serializable id);

    /**
     * 分页查询数据集详情列表
     * @param page 分页参数
     * @param dto 查询条件
     * @return 分页结果
     */
    IPage<AiDatasetDto> pageOfDetail(Page<AiDataset> page, AiDatasetDto dto);
}
