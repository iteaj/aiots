package com.iteaj.iboot.module.iot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.entity.EventSource;
import com.iteaj.iboot.module.iot.entity.Linkage;
import com.iteaj.iboot.module.iot.dto.LinkageDto;
import com.iteaj.iboot.module.iot.entity.LinkageCondition;
import com.iteaj.iboot.module.iot.entity.LinkageItem;
import com.iteaj.iboot.module.iot.linkage.LinkageCacheService;
import com.iteaj.iboot.module.iot.mapper.LinkageItemMapper;
import com.iteaj.iboot.module.iot.mapper.LinkageMapper;
import com.iteaj.iboot.module.iot.service.IEventSourceService;
import com.iteaj.iboot.module.iot.service.ILinkageActionService;
import com.iteaj.iboot.module.iot.service.ILinkageConditionService;
import com.iteaj.iboot.module.iot.service.ILinkageService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final IEventSourceService eventSourceService;
    private final LinkageCacheService linkageCacheService;
    private final ILinkageActionService linkageActionService;
    private final ILinkageConditionService linkageConditionService;

    public LinkageServiceImpl(ILinkageActionService linkageActionService
            , LinkageItemMapper linkageItemMapper, IEventSourceService eventSourceService
            , LinkageCacheService linkageCacheService, ILinkageConditionService linkageConditionService) {
        this.linkageActionService = linkageActionService;
        this.linkageItemMapper = linkageItemMapper;
        this.eventSourceService = eventSourceService;
        this.linkageCacheService = linkageCacheService;
        this.linkageConditionService = linkageConditionService;
    }

    @Override
    public BooleanResult save(Linkage entity) {
        this.getOne(Wrappers.<Linkage>lambdaQuery().eq(Linkage::getName, entity.getName()))
                .ifPresentThrow("名称已存在["+entity.getName()+"]");
        return super.save(entity);
    }

    @Override
    public BooleanResult updateById(Linkage entity) {
        this.getOne(Wrappers.<Linkage>lambdaQuery()
                .ne(Linkage::getId, entity.getId())
                .eq(Linkage::getName, entity.getName()))
                .ifPresentThrow("名称已存在["+entity.getName()+"]").ifPresent(item -> {
                    if(item.getStatus() == FuncStatus.enabled) {
                        throw new ServiceException("请先停用此场景联动");
                    }
        });
        return super.updateById(entity);
    }

    @Override
    public DetailResult<LinkageDto> detailById(Long id) {
        LinkageDto dto = this.getBaseMapper().detailById(id);
        return new DetailResult<>(dto);
    }

    @Override
    public DetailResult<LinkageDto> joinProductById(Long id) {
        return new DetailResult<>(this.getBaseMapper().joinProductById(id));
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

        // 校验是否已经存在
        LinkageItem linkageItem = linkageItemMapper.selectOne(Wrappers.<LinkageItem>lambdaQuery()
                .eq(LinkageItem::getLinkageId, item.getLinkageId())
                .eq(LinkageItem::getName, item.getName())
                .ne(item.getId() != null, LinkageItem::getId, item.getId()));
        if(linkageItem != null) {
            return BooleanResult.buildFalse("联动项名已存在["+item.getName()+"]");
        }

        if(item.getId() != null) { // 如果是更新则移除原来的所有记录
            this.getBaseMapper().removeJoinByItemId(item.getId());
            this.linkageItemMapper.updateById(item);
        } else {
            linkageItemMapper.insert(item);
        }

        item.getActions().forEach(action -> action.setLinkageId(item.getLinkageId()).setLinkageItemId(item.getId()));

        linkageActionService.saveBatch(item.getActions());
        for (int i = 0; i < item.getConditions().size(); i++) {
            LinkageCondition condition = item.getConditions().get(i);
            condition.setSort(i * 3 + 3).setLinkageId(item.getLinkageId()).setLinkageItemId(item.getId());
            linkageConditionService.save(condition);
        }

        return BooleanResult.buildTrue("更新成功");
    }

    @Override
    @Transactional
    public BooleanResult removeAllByIds(List<Long> idList) {
        if(CollectionUtil.isEmpty(idList)) {
            return BooleanResult.buildFalse("未指定删除记录");
        }

        if(count(Wrappers.<Linkage>lambdaQuery().in(Linkage::getId,  idList).eq(Linkage::getStatus, FuncStatus.enabled)).getData() > 0) {
            return BooleanResult.buildFalse("请先关闭处于运行状态中的场景");
        }

        int i = this.getBaseMapper().removeAllByIds(idList);
        return new BooleanResult(i > 0);
    }

    @Override
    public DetailResult<LinkageItem> detailItemById(Long id) {
        return new DetailResult<>(this.getBaseMapper().detailItemById(id));
    }

    @Override
    public BooleanResult switchStatus(Long id, FuncStatus status) {
        if(id == null) {
            return BooleanResult.buildFalse("未指定要操作的id");
        }
        if(status == null) {
            return BooleanResult.buildFalse("未指定要切换的状态");
        }

        this.detailById(id).ifNotPresentThrow("不存在事件联动").ifPresent(linkageDto -> {
            if(status == FuncStatus.enabled) {
                if(CollectionUtil.isNotEmpty(linkageDto.getItems())) {
                    linkageDto.getItems().forEach(item -> {
                        if(item.useAllGroup()) { // 使用和事件源相同的所有组
                            item.setDeviceGroupIds(linkageDto.getSourceDeviceGroupIds());
                        }
                    });

                    linkageCacheService.register(linkageDto.getEventSourceId(), linkageDto.getItems());
                } else {
                    throw new ServiceException("没有配置联动项");
                }
            } else {
                linkageCacheService.removeLinkageItemsByLinkageId(id);
            }
        });

        return this.update(Wrappers.<Linkage>lambdaUpdate().set(Linkage::getStatus, status).eq(Linkage::getId, id));
    }

    @Override
    public PageResult<IPage<Linkage>> detailPage(Page<Linkage> page, Linkage entity) {
        this.getBaseMapper().detailPage(page, entity);
        return new PageResult<>(page);
    }

    @Override
    public ListResult<LinkageDto> listDetailOfEnabled() {
        List<LinkageDto> linkages = this.getBaseMapper().listDetailOfEnabled();
        return new ListResult<>(linkages);
    }
}
