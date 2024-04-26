package com.iteaj.iboot.module.iot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.IBaseService;
import com.iteaj.framework.result.PageResult;
import com.iteaj.iboot.module.iot.dto.CollectDataDto;
import com.iteaj.iboot.module.iot.dto.CountLastWeekDto;
import com.iteaj.iboot.module.iot.entity.CollectData;

import java.util.List;

public interface ICollectDataService extends IBaseService<CollectData> {

    PageResult<Page<CollectDataDto>> detailOfPage(Page page, CollectDataDto entity);

    /**
     * 统计最近一周的数据
     * @param deviceGroupId
     * @return
     */
    List<CountLastWeekDto> countLastWeek(Long deviceGroupId);
}
