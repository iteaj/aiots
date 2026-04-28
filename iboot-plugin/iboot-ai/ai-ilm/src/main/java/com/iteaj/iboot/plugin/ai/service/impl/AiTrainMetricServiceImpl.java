package com.iteaj.iboot.plugin.ai.service.impl;

import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.iboot.plugin.ai.entity.AiTrainMetric;
import com.iteaj.iboot.plugin.ai.mapper.AiTrainMetricMapper;
import com.iteaj.iboot.plugin.ai.service.IAiTrainMetricService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * AI训练指标服务实现
 * @author aiots-dev
 */
@Slf4j
@Service
public class AiTrainMetricServiceImpl extends BaseServiceImpl<AiTrainMetricMapper, AiTrainMetric> implements IAiTrainMetricService {
}
