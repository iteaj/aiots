package com.iteaj.iboot.module.iot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iboot.module.iot.entity.ModelApiConfig;
import com.iteaj.iboot.module.iot.mapper.ModelApiMapper;
import com.iteaj.iboot.module.iot.service.IModelApiConfigService;
import com.iteaj.iboot.module.iot.service.IModelApiService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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

    private final IModelApiConfigService modelApiConfigService;

    public ModelApiServiceImpl(IModelApiConfigService modelApiConfigService) {
        this.modelApiConfigService = modelApiConfigService;
    }

    @Override
    @Transactional
    public BooleanResult save(ModelApi entity) {
        entity.setCreateTime(new Date());
        this.getByProductIdAndCode(entity.getProductId(), entity.getCode())
                .ifPresentThrow("接口代码已存在["+entity.getCode()+"]");

        if(!CollectionUtils.isEmpty(entity.getDownConfig())) {
            entity.getDownConfig().forEach(item -> resolverModelApiConfig(entity, item));
            this.modelApiConfigService.saveBatch(entity.getDownConfig());
        }

        if(!CollectionUtils.isEmpty(entity.getUpConfig())) {
            entity.getUpConfig().forEach(item -> resolverModelApiConfig(entity, item));
            this.modelApiConfigService.saveBatch(entity.getUpConfig());
        }

        return super.save(entity);
    }

    private void resolverModelApiConfig(ModelApi entity, ModelApiConfig config) {
        config.setApiCode(entity.getCode());
        if(StrUtil.isNotBlank(config.getValue())) {
            if(config.getValue().startsWith("@")) {
                if(StrUtil.isBlank(config.getAttrField())) {
                    config.setAttrField(config.getValue().substring(1));
                }
            } else {
                config.setAttrField(null).setModelAttrId(null);
            }
        }
    }

    @Override
    @Transactional
    public BooleanResult updateById(ModelApi entity) {
        this.getByProductIdAndCode(entity.getProductId(), entity.getCode()).ifPresent(item -> {
           if(!item.getId().equals(entity.getId())) {
               throw new ServiceException("接口代码已存在["+entity.getCode()+"]");
           }
        });

        return super.updateById(entity).ifPresentAndTrue(status -> {
            if(!CollectionUtils.isEmpty(entity.getDownConfig())) {
                entity.getDownConfig().forEach(item -> resolverModelApiConfig(entity, item));
                ModelApiConfig modelApiConfig = entity.getDownConfig().get(0);
                if(modelApiConfig.getId() == null) {
                    // 删除此接口的所有配置
                    this.modelApiConfigService.remove(Wrappers.<ModelApiConfig>lambdaQuery().eq(ModelApiConfig::getApiCode, entity.getCode()));
                    // 重新添加配置
                    this.modelApiConfigService.saveBatch(entity.getDownConfig());
                } else {
                    // 更新配置
                    this.modelApiConfigService.updateBatchById(entity.getDownConfig());
                }
            }

            if(!CollectionUtils.isEmpty(entity.getUpConfig())) {
                entity.getUpConfig().forEach(item -> resolverModelApiConfig(entity, item));
                this.modelApiConfigService.saveOrUpdateBatch(entity.getUpConfig());
            }
        });
    }

    @Override
    @Transactional
    public BooleanResult removeByIds(Collection<? extends Serializable> idList) {
        listByIds(idList).forEach(item -> {
            modelApiConfigService.remove(Wrappers.<ModelApiConfig>lambdaQuery()
                    .eq(ModelApiConfig::getApiCode, item.getCode())
                    .eq(ModelApiConfig::getProductId, item.getProductId()));
        });
        return super.removeByIds(idList);
    }

    @Override
    public ListResult<ModelApi> detailsOfProductId(Long productId) {
        List<ModelApi> list = this.getBaseMapper().detailsOfProductId(productId);
        return new ListResult<>(list);
    }

    @Override
    public DetailResult<ModelApi> getByProductIdAndCode(Long productId, String code) {
        return this.getOne(Wrappers.<ModelApi>lambdaQuery()
                .eq(ModelApi::getProductId, productId)
                .eq(ModelApi::getCode, code));
    }

    @Override
    public DetailResult<ModelApi> getByCode(String code) {
        return this.getOne(Wrappers.<ModelApi>lambdaQuery().eq(ModelApi::getCode, code));
    }

    @Override
    public Result<ModelApi> detailById(Long id) {
        return new DetailResult<>(this.getBaseMapper().detailById(id));
    }

    @Override
    public BooleanResult updateAsStatus(Long productId, String code) {
        if(productId == null || StrUtil.isBlank(code)) {
            return BooleanResult.buildFalse("参数错误[productId="+productId+", code="+code+"]");
        }

        return new BooleanResult(this.getBaseMapper().updateAsStatus(productId, code) > 0);
    }
}
