package com.iteaj.iboot.msn.iot.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.iboot.msn.iot.entity.CollectDetail;
import com.iteaj.iboot.msn.iot.mapper.CollectDetailMapper;
import com.iteaj.iboot.msn.iot.service.ICollectDetailService;
import org.springframework.stereotype.Service;

@Service
public class CollectDetailServiceImpl extends BaseServiceImpl<CollectDetailMapper, CollectDetail> implements ICollectDetailService {

    @Override
    public PageResult<Page<CollectDetail>> detailPage(Page page, CollectDetail entity) {
        Page<CollectDetail> detailPage = this.baseMapper.detailPage(page, entity);
        return new PageResult<>(detailPage);
    }

    @Override
    public DetailResult<CollectDetail> detailById(Long id) {
        return new DetailResult<>(this.baseMapper.detailById(id));
    }
}
