package com.iteaj.iboot.module.iot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.iboot.module.iot.entity.Linkage;
import com.iteaj.iboot.module.iot.dto.LinkageDto;
import com.iteaj.iboot.module.iot.entity.LinkageCondition;
import com.iteaj.iboot.module.iot.entity.LinkageItem;
import com.iteaj.iboot.module.iot.mapper.LinkageItemMapper;
import com.iteaj.iboot.module.iot.mapper.LinkageMapper;
import com.iteaj.iboot.module.iot.service.ILinkageActionService;
import com.iteaj.iboot.module.iot.service.ILinkageConditionService;
import com.iteaj.iboot.module.iot.service.ILinkageService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 场景联动 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2024-02-29
 */
@Service
public class LinkageServiceImpl extends BaseServiceImpl<LinkageMapper, Linkage> implements ILinkageService {

    private final LinkageItemMapper linkageItemMapper;
    private final ILinkageActionService linkageActionService;
    private final ILinkageConditionService linkageConditionService;

    public LinkageServiceImpl(ILinkageActionService linkageActionService
            , LinkageItemMapper linkageItemMapper, ILinkageConditionService linkageConditionService) {
        this.linkageActionService = linkageActionService;
        this.linkageItemMapper = linkageItemMapper;
        this.linkageConditionService = linkageConditionService;
    }

    @Override
    public BooleanResult save(Linkage entity) {
        this.getOne(Wrappers.<Linkage>lambdaQuery().eq(Linkage::getName, entity.getName()))
                .ifPresentThrow("名称已存在["+entity.getName()+"]");
        this.getOne(Wrappers.<Linkage>lambdaQuery().eq(Linkage::getEventSourceId, entity.getEventSourceId()))
                .ifPresentThrow("事件源已被绑定");
        return super.save(entity);
    }

    @Override
    public BooleanResult updateById(Linkage entity) {
        this.getOne(Wrappers.<Linkage>lambdaQuery()
                .ne(Linkage::getId, entity.getId())
                .eq(Linkage::getName, entity.getName()))
                .ifPresentThrow("名称已存在["+entity.getName()+"]");
        this.getOne(Wrappers.<Linkage>lambdaQuery()
                .ne(Linkage::getId, entity.getId())
                .eq(Linkage::getEventSourceId, entity.getEventSourceId()))
                .ifPresentThrow("事件源已被绑定");
        return super.updateById(entity);
    }

    @Override
    public DetailResult<LinkageDto> detailById(Long id) {
        LinkageDto dto = this.getBaseMapper().detailById(id);
        return new DetailResult<>(dto);
    }

    @Override
    public DetailResult<LinkageDto> joinDeviceGroupAndProductById(Long id) {
        return new DetailResult<>(this.getBaseMapper().joinDeviceGroupAndProductById(id));
    }

    @Override
    @Transactional
    public BooleanResult removeByItemIds(List<Long> itemIds) {
        if(CollectionUtil.isEmpty(itemIds)) {
            return BooleanResult.buildFalse("没有指定删除项");
        }

        return new BooleanResult(this.getBaseMapper().removeByItemIds(itemIds));
    }

    @Override
    @Transactional
    public BooleanResult saveOrUpdateItem(LinkageItem item) {
        if(item.getLinkageId() == null) {
            return BooleanResult.buildFalse("未指定所属联动场景");
        }

        if(CollectionUtil.isEmpty(item.getActions())) {
            return BooleanResult.buildFalse("未配置联动动作");
        }

        if(CollectionUtil.isEmpty(item.getConditions())) {
            return BooleanResult.buildFalse("未配置联动条件");
        }

        for (int i = 0; i < item.getConditions().size(); i++) {
            LinkageCondition condition = item.getConditions().get(i);
            if(CollectionUtil.isEmpty(condition.getItems())) {
                return BooleanResult.buildFalse("存在未配置条件项错误");
            }
        }

        if(item.getId() != null) { // 移除原来的所有记录
            this.removeByItemIds(Arrays.asList(item.getId()));
            item.setId(null);
        }

        int insert = linkageItemMapper.insert(item);
        item.getActions().forEach(action -> action.setLinkageId(item.getLinkageId()).setLinkageItemId(item.getId()));

        linkageActionService.saveBatch(item.getActions());
        item.getConditions().forEach(condition -> {
            condition.setLinkageId(item.getLinkageId()).setLinkageItemId(item.getId());
            linkageConditionService.save(condition);
        });

        return new BooleanResult(insert > 0);
    }

    @Override
    @Transactional
    public BooleanResult removeAllByIds(List<Long> idList) {
        int i = this.getBaseMapper().removeAllByIds(idList);
        return new BooleanResult(i > 0);
    }
}
