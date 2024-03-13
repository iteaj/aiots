package com.iteaj.iboot.module.iot.service;

import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.Result;
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

    DetailResult<LinkageDto> joinDeviceGroupAndProductById(Long id);

    BooleanResult removeByItemIds(List<Long> itemIds);

    BooleanResult saveOrUpdateItem(LinkageItem item);

    BooleanResult removeAllByIds(List<Long> idList);
}
