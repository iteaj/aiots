package com.iteaj.iboot.module.iot.mapper;

import com.iteaj.framework.result.DetailResult;
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
}
