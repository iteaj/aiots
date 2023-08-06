package com.iteaj.iboot.module.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.dto.CollectDataDto;
import com.iteaj.iboot.module.iot.entity.CollectData;

public interface CollectDataMapper extends BaseMapper<CollectData> {

    Page<CollectDataDto> detailOfPage(Page page, CollectDataDto entity);
}
