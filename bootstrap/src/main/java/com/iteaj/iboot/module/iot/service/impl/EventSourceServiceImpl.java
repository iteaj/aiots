package com.iteaj.iboot.module.iot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.iboot.module.iot.entity.EventSource;
import com.iteaj.iboot.module.iot.entity.EventSourceDetail;
import com.iteaj.iboot.module.iot.mapper.EventSourceMapper;
import com.iteaj.iboot.module.iot.service.IEventSourceDetailService;
import com.iteaj.iboot.module.iot.service.IEventSourceService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 事件源 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2024-02-27
 */
@Service
public class EventSourceServiceImpl extends BaseServiceImpl<EventSourceMapper, EventSource> implements IEventSourceService {

    private final IEventSourceDetailService eventSourceDetailService;

    public EventSourceServiceImpl(IEventSourceDetailService eventSourceDetailService) {
        this.eventSourceDetailService = eventSourceDetailService;
    }

    @Override
    @Transactional
    public BooleanResult save(EventSource entity) {
        this.getOne(Wrappers.<EventSource>lambdaQuery()
                .eq(EventSource::getName, entity.getName()))
                .ifPresentThrow("名称已存在["+entity.getName()+"]");
        entity.setCreateTime(new Date());
        BooleanResult save = super.save(entity);
        if(CollectionUtil.isNotEmpty(entity.getModelApiIds())) {
            List<EventSourceDetail> sourceDetails = entity.getModelApiIds().stream()
                    .map(item -> new EventSourceDetail().setEventSourceId(entity.getId()).setModelApiId(Long.valueOf(item)))
                    .collect(Collectors.toList());
            eventSourceDetailService.saveBatch(sourceDetails);
        } else {
            return BooleanResult.buildFalse("未配置物模型接口");
        }

        return save;
    }

    @Override
    @Transactional
    public BooleanResult updateById(EventSource entity) {
        this.getOne(Wrappers.<EventSource>lambdaQuery()
                .ne(EventSource::getId, entity.getId())
                .eq(EventSource::getName, entity.getName()))
                .ifPresentThrow("名称已存在["+entity.getName()+"]");
        if(CollectionUtil.isNotEmpty(entity.getModelApiIds())) {
            eventSourceDetailService.remove(Wrappers.<EventSourceDetail>lambdaQuery()
                    .eq(EventSourceDetail::getEventSourceId, entity.getId()));

            List<EventSourceDetail> sourceDetails = entity.getModelApiIds().stream()
                    .map(item -> new EventSourceDetail().setEventSourceId(entity.getId()).setModelApiId(Long.valueOf(item)))
                    .collect(Collectors.toList());
            eventSourceDetailService.saveBatch(sourceDetails);
        } else {
            return BooleanResult.buildFalse("未配置物模型接口");
        }

        return super.updateById(entity);
    }

    @Override
    public PageResult<IPage<EventSource>> detailPage(Page<EventSource> page, EventSource entity) {
        this.getBaseMapper().detailPage(page, entity);
        return new PageResult<>(page);
    }

    @Override
    public DetailResult<EventSource> detailById(Long id) {
        return new DetailResult<>(this.getBaseMapper().detailById(id));
    }
}
