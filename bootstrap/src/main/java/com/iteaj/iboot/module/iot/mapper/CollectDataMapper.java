package com.iteaj.iboot.module.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.dto.CollectDataDto;
import com.iteaj.iboot.module.iot.dto.CountLastWeekDto;
import com.iteaj.iboot.module.iot.dto.CountLastTimeDto;
import com.iteaj.iboot.module.iot.entity.CollectData;

import java.util.List;

public interface CollectDataMapper extends BaseMapper<CollectData> {

    Page<CollectDataDto> detailOfPage(Page page, CollectDataDto entity);

    /**
     * 统计最近一周的数据
     * @param deviceGroupId
     * @return
     */
    List<CountLastWeekDto> countLastWeek(Long deviceGroupId);

    /**
     * 统计最近指定天的数据
     * @param days 天数
     * @return
     */
    List<CountLastTimeDto> countLastByDays(int days);
}
