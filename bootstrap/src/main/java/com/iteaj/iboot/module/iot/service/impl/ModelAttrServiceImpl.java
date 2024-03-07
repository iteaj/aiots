package com.iteaj.iboot.module.iot.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.iot.entity.ModelAttr;
import com.iteaj.iboot.module.iot.entity.ModelAttrDict;
import com.iteaj.iboot.module.iot.mapper.ModelAttrDictMapper;
import com.iteaj.iboot.module.iot.mapper.ModelAttrMapper;
import com.iteaj.iboot.module.iot.service.IModelAttrService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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

    @Autowired
    private ModelAttrDictMapper modelAttrDictMapper;

    @Override
    @Transactional
    public BooleanResult save(ModelAttr entity) {
        this.getOne(Wrappers.<ModelAttr>lambdaQuery()
                .eq(ModelAttr::getField, entity.getField())
                .eq(ModelAttr::getProductId, entity.getProductId()))
                .ifPresentThrow("属性字段已存在["+entity.getField()+"]");
        entity.setCreateTime(new Date());
        resolveField(entity);
        BooleanResult booleanResult = super.save(entity).ifPresent(item -> {
            if(!CollectionUtils.isEmpty(entity.getDicts())) {
                entity.getDicts().forEach(dict -> {
                    dict.setModelAttrId(entity.getId());
                    modelAttrDictMapper.insert(dict);
                });
            }
        });

        return booleanResult;
    }

    @Override
    public BooleanResult updateById(ModelAttr entity) {
        this.getOne(Wrappers.<ModelAttr>lambdaQuery()
                .ne(ModelAttr::getId, entity.getId())
                .eq(ModelAttr::getField, entity.getField())
                .eq(ModelAttr::getProductId, entity.getProductId()))
                .ifPresentThrow("属性字段已存在["+entity.getField()+"]");
        resolveField(entity);
        return super.updateById(entity);
    }

    private void resolveField(ModelAttr entity) {
        if(entity.getType() == null) {
            throw new ServiceException("类型必填");
        }
        entity.setRealType(entity.getType());
        if(entity.getType().startsWith("[")) {
            entity.setEnumerate(true); // 属于枚举类型
            String realType = entity.getType().substring(1, entity.getType().length() - 1); // 真实类型
            entity.setRealType(realType);
        }
    }

    @Override
    @Transactional
    public BooleanResult removeByIds(Collection<? extends Serializable> idList) {
        return super.removeByIds(idList).ifPresent(item -> {
            if(Boolean.TRUE.equals(item)) {
                modelAttrDictMapper.delete(Wrappers.<ModelAttrDict>lambdaQuery()
                        .in(ModelAttrDict::getModelAttrId, idList));
            }
        });
    }

    @Override
    public DetailResult<ModelAttr> detailById(Long id) {
        return new DetailResult<>(this.getBaseMapper().detailById(id));
    }

    @Override
    public Result<Boolean> saveOrUpdateModelAttrDict(ModelAttrDict entity) {
        if(entity.getId() == null) {
            return new BooleanResult(modelAttrDictMapper.insert(entity) > 0);
        } else {
            return new BooleanResult(modelAttrDictMapper.updateById(entity) > 0);
        }
    }

    @Override
    @Transactional
    public Result<Boolean> removeModelAttrDictByIds(List<Long> idList) {
        if(!CollectionUtils.isEmpty(idList)) {
            modelAttrDictMapper.deleteBatchIds(idList);
        }

        return new BooleanResult(true);
    }

    @Override
    public ListResult<ModelAttrDict> listModelAttrDicts(ModelAttrDict entity) {
        List<ModelAttrDict> modelAttrDicts = modelAttrDictMapper.selectList(Wrappers.lambdaQuery(entity));
        return new ListResult<>(modelAttrDicts);
    }
}
