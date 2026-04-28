package com.iteaj.iboot.plugin.ai.service.impl;

import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.iboot.plugin.ai.entity.AiDatasetItem;
import com.iteaj.iboot.plugin.ai.mapper.AiDatasetItemMapper;
import com.iteaj.iboot.plugin.ai.service.IAiDatasetItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * AI数据集明细服务实现
 * @author aiots-dev
 */
@Slf4j
@Service
public class AiDatasetItemServiceImpl extends BaseServiceImpl<AiDatasetItemMapper, AiDatasetItem> implements IAiDatasetItemService {
}
