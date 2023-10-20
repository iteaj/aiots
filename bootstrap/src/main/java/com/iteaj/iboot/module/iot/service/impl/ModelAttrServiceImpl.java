package com.iteaj.iboot.module.iot.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.iboot.module.iot.entity.ModelAttr;
import com.iteaj.iboot.module.iot.mapper.ModelAttrMapper;
import com.iteaj.iboot.module.iot.service.IModelAttrService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 物模型属性 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2023-09-12
 */
@Service
public class ModelAttrServiceImpl extends BaseServiceImpl<ModelAttrMapper, ModelAttr> implements IModelAttrService {

    @Override
    public BooleanResult save(ModelAttr entity) {
        this.getOne(Wrappers.<ModelAttr>lambdaQuery()
                .eq(ModelAttr::getField, entity.getField())
                .eq(ModelAttr::getProductId, entity.getProductId()))
                .ifPresentThrow("属性字段已存在["+entity.getField()+"]");
        return super.save(entity);
    }

    @Override
    public BooleanResult updateById(ModelAttr entity) {
        this.getOne(Wrappers.<ModelAttr>lambdaQuery()
                .ne(ModelAttr::getId, entity.getId())
                .eq(ModelAttr::getField, entity.getField())
                .eq(ModelAttr::getProductId, entity.getProductId()))
                .ifPresentThrow("属性字段已存在["+entity.getField()+"]");
        return super.updateById(entity);
    }
}
