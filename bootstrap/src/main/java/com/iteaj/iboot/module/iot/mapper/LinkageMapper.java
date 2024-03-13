package com.iteaj.iboot.module.iot.mapper;

import com.iteaj.iboot.module.iot.entity.Linkage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iteaj.iboot.module.iot.dto.LinkageDto;

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

    LinkageDto joinDeviceGroupAndProductById(Long id);

    Boolean removeByItemIds(List<Long> itemIds);

    int removeAllByIds(List<Long> idList);
}
