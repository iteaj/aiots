package com.iteaj.iboot.module.iot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.PageResult;
import com.iteaj.iboot.module.iot.dto.CountLastWeekDto;
import com.iteaj.iboot.module.iot.entity.WarnRecord;
import com.iteaj.iboot.module.iot.mapper.WarnRecordMapper;
import com.iteaj.iboot.module.iot.service.IWarnRecordService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 告警记录 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2024-02-29
 */
@Service
public class WarnRecordServiceImpl extends BaseServiceImpl<WarnRecordMapper, WarnRecord> implements IWarnRecordService {

    @Override
    public PageResult<IPage<WarnRecord>> detailPage(Page page, WarnRecord entity) {
        this.getBaseMapper().detailPage(page, entity);
        return new PageResult<>(page);
    }

    @Override
    public List<WarnRecord> listByDeviceGroupId(Long groupId, Integer size) {
        return this.getBaseMapper().listByDeviceGroupId(groupId, size);
    }

    @Override
    public List<CountLastWeekDto> countLastWeek(Long deviceGroupId) {
        Map<String, CountLastWeekDto> map = new HashMap<>();
        List<CountLastWeekDto> dtos = this.getBaseMapper().countLastWeek(deviceGroupId);
        if(CollectionUtil.isNotEmpty(dtos)) {
            map = dtos.stream().collect(Collectors.toMap(CountLastWeekDto::getTime, item -> item));
        }

        List<CountLastWeekDto> result = new ArrayList<>();
        DateTime lastWeek = new DateTime().offset(DateField.DAY_OF_MONTH, -6);
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
