package com.iteaj.iboot.module.iot.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.iboot.module.iot.entity.Linkage;
import com.iteaj.iboot.module.iot.dto.LinkageDto;
import com.iteaj.iboot.module.iot.mapper.LinkageMapper;
import com.iteaj.iboot.module.iot.service.ILinkageService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

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
                .ifPresentThrow("名称已存在["+entity.getName()+"]");
        return super.updateById(entity);
    }

    @Override
    public DetailResult<LinkageDto> detailById(Long id) {
        LinkageDto dto = this.getBaseMapper().detailById(id);
        return new DetailResult<>(dto);
    }
}
