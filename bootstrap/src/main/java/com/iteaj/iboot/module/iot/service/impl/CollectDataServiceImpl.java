package com.iteaj.iboot.module.iot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.result.PageResult;
import com.iteaj.iboot.module.iot.dto.CollectDataDto;
import com.iteaj.iboot.module.iot.dto.CountLastWeekDto;
import com.iteaj.iboot.module.iot.entity.CollectData;
import com.iteaj.iboot.module.iot.mapper.CollectDataMapper;
import com.iteaj.iboot.module.iot.service.ICollectDataService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CollectDataServiceImpl  extends BaseServiceImpl<CollectDataMapper, CollectData> implements ICollectDataService {

    @Override
    public PageResult<Page<CollectDataDto>> detailOfPage(Page page, CollectDataDto entity) {
        return new PageResult<>(this.baseMapper.detailOfPage(page, entity));
    }

    @Override
    public List<CountLastWeekDto> countLastWeek(Long deviceGroupId) {
        Map<String, CountLastWeekDto> map = new HashMap<>();
        List<CountLastWeekDto> dtos = this.getBaseMapper().countLastWeek(deviceGroupId);
        if(CollectionUtil.isNotEmpty(dtos)) {
            map = dtos.stream().collect(Collectors.toMap(CountLastWeekDto::getTime, item -> item));
        }

        List<CountLastWeekDto> result = new ArrayList<>();
        DateTime lastWeek = new DateTime().offset(DateField.DAY_OF_MONTH, -7);
        for (int i = 0; i < 7; i++) {
            String time = lastWeek.toString("yyyy-MM-dd");
            CountLastWeekDto lastWeekDto = map.get(time);
            if(lastWeekDto != null) {
                result.add(lastWeekDto);
            } else {
                result.add(new CountLastWeekDto().buildZero(time));
            }

            lastWeek = lastWeek.offset(DateField.DAY_OF_MONTH, 1);
        }

        return result;
    }
}
