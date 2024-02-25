package com.iteaj.iboot.module.iot.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.result.PageResult;
import com.iteaj.iboot.module.iot.dto.CollectDataDto;
import com.iteaj.iboot.module.iot.entity.CollectData;
import com.iteaj.iboot.module.iot.mapper.CollectDataMapper;
import com.iteaj.iboot.module.iot.service.ICollectDataService;
import org.springframework.stereotype.Service;

@Service
public class CollectDataServiceImpl  extends BaseServiceImpl<CollectDataMapper, CollectData> implements ICollectDataService {

    @Override
    public PageResult<Page<CollectDataDto>> detailOfPage(Page page, CollectDataDto entity) {
        return new PageResult<>(this.baseMapper.detailOfPage(page, entity));
    }
}
