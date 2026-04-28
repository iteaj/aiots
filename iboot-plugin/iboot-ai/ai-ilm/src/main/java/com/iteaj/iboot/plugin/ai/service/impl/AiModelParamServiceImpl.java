package com.iteaj.iboot.plugin.ai.service.impl;

import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.iboot.plugin.ai.entity.AiModelParam;
import com.iteaj.iboot.plugin.ai.mapper.AiModelParamMapper;
import com.iteaj.iboot.plugin.ai.service.IAiModelParamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * AI模型参数服务实现
 * @author aiots-dev
 */
@Slf4j
@Service
public class AiModelParamServiceImpl extends BaseServiceImpl<AiModelParamMapper, AiModelParam> implements IAiModelParamService {
}
