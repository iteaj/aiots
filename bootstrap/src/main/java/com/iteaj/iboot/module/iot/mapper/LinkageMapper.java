package com.iteaj.iboot.module.iot.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.iboot.module.iot.dto.FuncStatusProfileDto;
import com.iteaj.iboot.module.iot.dto.GroupAndProductParamDto;
import com.iteaj.iboot.module.iot.entity.Linkage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iteaj.iboot.module.iot.dto.LinkageDto;
import com.iteaj.iboot.module.iot.entity.LinkageItem;

import java.util.List;

/**
 * <p>
 * 场景联动 Mapper 接口
 * </p>
 *
 * @author iteaj
 * @since 2024-02-29
 */
public interface LinkageMapper extends BaseMapper<Linkage> {

    LinkageDto detailById(Long id);

    LinkageDto joinProductById(Long id);

    Boolean removeByItemIds(List<Long> itemIds);

    int removeAllByIds(List<Long> idList);

    LinkageItem detailItemById(Long id);

    /**
     * 移除和这个项id关联的所有记录(不包含项本身)
     * @param itemId
     */
    void removeJoinByItemId(Long itemId);

    IPage<Linkage> detailPage(Page<Linkage> page, Linkage entity);

    List<LinkageDto> listDetailExecOfEnabled();

    LinkageDto joinEventSourceById(Long id);

    LinkageDto detailExecById(Long id);

    /**
     * 统计场景联动下状态信息
     * @param param
     * @return
     */
    FuncStatusProfileDto countStatusProfile(GroupAndProductParamDto param);
}
