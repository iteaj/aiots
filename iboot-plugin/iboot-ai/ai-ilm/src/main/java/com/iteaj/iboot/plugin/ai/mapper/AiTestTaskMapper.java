package com.iteaj.iboot.plugin.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.plugin.ai.dto.AiTestTaskDto;
import com.iteaj.iboot.plugin.ai.entity.AiTestTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;

/**
 * AI模型测试任务Mapper
 * @author aiots-dev
 */
@Mapper
public interface AiTestTaskMapper extends BaseMapper<AiTestTask> {

    /**
     * 根据ID查询测试任务详情（含训练任务名称）
     * @param id 任务ID
     * @return 测试任务详情DTO
     */
    AiTestTaskDto detailById(@Param("id") Serializable id);

    /**
     * 分页查询测试任务列表（含训练任务名称）
     * @param page 分页参数
     * @param dto  查询条件
     * @return 分页结果
     */
    IPage<AiTestTaskDto> pageOfDetail(Page<AiTestTask> page, @Param("entity") AiTestTaskDto dto);
}
