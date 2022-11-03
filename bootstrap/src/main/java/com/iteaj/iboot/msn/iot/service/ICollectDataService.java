package com.iteaj.iboot.msn.iot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.IBaseService;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.msn.iot.dto.CollectDataDto;
import com.iteaj.iboot.msn.iot.entity.CollectData;

public interface ICollectDataService extends IBaseService<CollectData> {

    PageResult<Page<CollectDataDto>> detailOfPage(Page page, CollectDataDto entity);
}
