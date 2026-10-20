package com.iteaj.iboot.module.iot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.spi.iot.consts.CollectSearchType;
import com.iteaj.framework.spi.iot.consts.TimeCountCondition;
import com.iteaj.framework.spi.iot.data.*;
import com.iteaj.iboot.module.iot.dto.CollectDataDto;
import com.iteaj.iboot.module.iot.dto.CountLastWeekDto;
import com.iteaj.iboot.module.iot.entity.WarnRecord;
import com.iteaj.iboot.module.iot.mapper.WarnRecordMapper;
import com.iteaj.iboot.module.iot.service.IWarnRecordService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.*;
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
public class WarnRecordServiceImpl extends BaseServiceImpl<WarnRecordMapper, WarnRecord> implements IWarnRecordService, CollectWarnDataService {

    @Override
    public PageResult<?> detailPage(Page page, WarnData entity) {
        this.getBaseMapper().detailPage(page, entity);
        return new PageResult<>(page);
    }

    @Override
    public BooleanResult batchRemove(List ids) {
        return this.removeByIds(ids);
    }

    @Override
    public List<EchartsCountValue> countOfCommonByTime(TimeCountCondition condition) {
        List<EchartsCountValue> values = new ArrayList<>();
        condition.buildSqlConditions().forEach(item -> {
            List<BaseEchartsCount> counts = this.getBaseMapper().countOfCommonByTime(item);
            List<EchartsCountValue> countValues = condition.buildResult(item, counts);
            values.addAll(countValues);
        });

        return values;
    }

    @Override
    public List<CollectValueItem> listOfCommonByTime(CollectDataCondition condition) {
        List<CollectValueItem> values = new ArrayList<>();
        if(condition.getSearchType() == CollectSearchType.list) {
            condition.buildSqlConditions().forEach(item -> {
                List<WarnRecord> counts = this.getBaseMapper().listOfCommonByTime(item);
                List<CollectValueItem> valueItems = condition.buildResult(item, counts, 0);
                values.addAll(valueItems);
            });
        } else {
            condition.buildSqlConditions().forEach(item -> {
                if(item.getFields().size() > 1) {
                    throw new ServiceException("分页搜索不支持同时多个字段");
                }

                Page<WarnRecord> page = new Page<>(condition.getCurrent(), condition.getSize());
                this.getBaseMapper().pageOfCommonByTime(page, item);
                List<CollectValueItem> valueItems = condition.buildResult(item, page.getRecords(), page.getTotal());
                values.addAll(valueItems);
            });
        }

        return values;
    }

    @Override
    public List<WarnRecord> listByDeviceGroupId(Long groupId, Integer size) {
        return this.getBaseMapper().listByDeviceGroupId(groupId, size);
    }

    @Override
    public List<WarnRecord> listByUidAndTimeRange(String uid, Date startTime, Date endTime) {
        return this.getBaseMapper().listByUidAndTimeRange(uid, startTime, endTime);
    }

    @Override
    public PageResult<IPage<WarnRecord>> pageByUid(Page<WarnRecord> page, String uid) {
        IPage<WarnRecord> result = this.getBaseMapper().pageByUid(page, uid);
        return new PageResult<>(result);
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
