package com.iteaj.iboot.module.iot.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.ListResult;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iboot.module.iot.mapper.ModelApiMapper;
import com.iteaj.iboot.module.iot.service.IModelApiService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 物模型接口 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2023-09-12
 */
@Service
public class ModelApiServiceImpl extends BaseServiceImpl<ModelApiMapper, ModelApi> implements IModelApiService {

    @Override
    public BooleanResult save(ModelApi entity) {
        entity.setCreateTime(new Date());
        this.getByProductIdAndCode(entity.getProductId(), entity.getCode())
                .ifPresentThrow("接口代码已存在["+entity.getCode()+"]");
        return super.save(entity);
    }

    @Override
    public BooleanResult updateById(ModelApi entity) {
        this.getByProductIdAndCode(entity.getProductId(), entity.getCode()).ifPresent(item -> {
           if(!item.getId().equals(entity.getId())) {
               throw new ServiceException("接口代码已存在["+entity.getCode()+"]");
           }
        });

        return super.updateById(entity);
    }

    @Override
    public ListResult<ModelApi> detailsOfProductId(Long productId) {
        return null;
    }

    @Override
    public DetailResult<ModelApi> getByProductIdAndCode(Long productId, String code) {
        return this.getOne(Wrappers.<ModelApi>lambdaQuery()
                .eq(ModelApi::getProductId, productId)
                .eq(ModelApi::getCode, code));
    }
}
