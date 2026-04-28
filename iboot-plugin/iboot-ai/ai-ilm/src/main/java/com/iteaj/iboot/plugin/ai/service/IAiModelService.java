package com.iteaj.iboot.plugin.ai.service;

import com.iteaj.framework.IBaseService;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.iboot.plugin.ai.dto.AiModelDto;
import com.iteaj.iboot.plugin.ai.entity.AiModel;

import java.io.Serializable;

/**
 * AI模型服务接口
 * @author aiots-dev
 */
public interface IAiModelService extends IBaseService<AiModel> {

    /**
     * 根据ID查询模型详情（含参数列表）
     * @param id 模型ID
     * @return 模型详情DTO
     */
    DetailResult<AiModelDto> detailById(Serializable id);
}
