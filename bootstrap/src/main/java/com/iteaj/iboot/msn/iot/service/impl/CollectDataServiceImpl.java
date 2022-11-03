package com.iteaj.iboot.msn.iot.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.result.PageResult;
import com.iteaj.iboot.msn.iot.dto.CollectDataDto;
import com.iteaj.iboot.msn.iot.entity.CollectData;
import com.iteaj.iboot.msn.iot.mapper.CollectDataMapper;
import com.iteaj.iboot.msn.iot.service.ICollectDataService;
import org.springframework.stereotype.Service;

@Service
public class CollectDataServiceImpl  extends BaseServiceImpl<CollectDataMapper, CollectData> implements ICollectDataService {

    @Override
    public PageResult<Page<CollectDataDto>> detailOfPage(Page page, CollectDataDto entity) {
        return new PageResult<>(this.baseMapper.detailOfPage(page, entity));
    }
}
