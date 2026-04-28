package com.iteaj.iboot.plugin.ai.service.impl;

import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.iboot.plugin.ai.dto.AiModelDto;
import com.iteaj.iboot.plugin.ai.entity.AiModel;
import com.iteaj.iboot.plugin.ai.mapper.AiModelMapper;
import com.iteaj.iboot.plugin.ai.service.IAiModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * AI模型服务实现
 * @author aiots-dev
 */
@Slf4j
@Service
public class AiModelServiceImpl extends BaseServiceImpl<AiModelMapper, AiModel> implements IAiModelService {

    @Override
    public DetailResult<AiModelDto> detailById(Serializable id) {
        return new DetailResult<>(this.getBaseMapper().detailById(id));
    }
}
