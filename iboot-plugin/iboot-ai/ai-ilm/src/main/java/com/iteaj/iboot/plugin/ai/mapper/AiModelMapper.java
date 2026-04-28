package com.iteaj.iboot.plugin.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iteaj.iboot.plugin.ai.dto.AiModelDto;
import com.iteaj.iboot.plugin.ai.entity.AiModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;

/**
 * AI模型Mapper
 * @author aiots-dev
 */
@Mapper
public interface AiModelMapper extends BaseMapper<AiModel> {

    /**
     * 根据ID查询模型详情（含参数列表）
     * @param id 模型ID
     * @return 模型详情DTO
     */
    AiModelDto detailById(@Param("id") Serializable id);
}
