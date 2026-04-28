package com.iteaj.iboot.plugin.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.plugin.ai.dto.AiTrainTaskDto;
import com.iteaj.iboot.plugin.ai.entity.AiTrainTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;

/**
 * AI训练任务Mapper
 * @author aiots-dev
 */
@Mapper
public interface AiTrainTaskMapper extends BaseMapper<AiTrainTask> {

    /**
     * 根据ID查询训练任务详情（含模型名称、训练指标列表）
     * @param id 任务ID
     * @return 训练任务详情DTO
     */
    AiTrainTaskDto detailById(@Param("id") Serializable id);

    /**
     * 分页查询训练任务列表（含模型名称）
     * @param page 分页参数
     * @param dto  查询条件
     * @return 分页结果
     */
    IPage<AiTrainTaskDto> pageOfDetail(Page<AiTrainTask> page, @Param("entity") AiTrainTaskDto dto);
}
