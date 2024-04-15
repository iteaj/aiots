package com.iteaj.iboot.module.iot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.*;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.entity.Linkage;
import com.iteaj.framework.IBaseService;
import com.iteaj.iboot.module.iot.dto.LinkageDto;
import com.iteaj.iboot.module.iot.entity.LinkageItem;

import java.util.List;

/**
 * <p>
 * 场景联动 服务类
 * </p>
 *
 * @author iteaj
 * @since 2024-02-29
 */
public interface ILinkageService extends IBaseService<Linkage> {

    DetailResult<LinkageDto> detailById(Long id);

    DetailResult<LinkageDto> joinProductById(Long id);

    BooleanResult removeByItemIds(List<Long> itemIds);

    BooleanResult saveOrUpdateItem(LinkageItem item);

    BooleanResult removeAllByIds(List<Long> idList);

    DetailResult<LinkageItem> detailItemById(Long id);

    BooleanResult switchStatus(Long id, FuncStatus status);

    PageResult<IPage<Linkage>> detailPage(Page<Linkage> page, Linkage entity);

    /**
     * 获取已经启用的所有场景联动
     * @return
     */
    ListResult<LinkageDto> listDetailExecOfEnabled();

    /**
     * 关联事件源
     * @param id
     * @return
     */
    DetailResult<LinkageDto> joinEventSourceById(Long id);
}
