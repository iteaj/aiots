package com.iteaj.iboot.module.iot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.iot.consts.CtrlMode;
import com.iteaj.framework.spi.iot.consts.PointProtocolConfig;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iboot.module.iot.entity.ModelApiConfig;
import com.iteaj.iboot.module.iot.mapper.ModelApiMapper;
import com.iteaj.iboot.module.iot.service.IModelApiConfigService;
import com.iteaj.iboot.module.iot.service.IModelApiService;
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
        if(StrUtil.isBlank(config.getAttrType())) {
            throw new ServiceException("属性类型[attrType]不能为空");
        }

        if(StrUtil.isBlank(config.getProtocolAttrField())) {
            throw new ServiceException("协议属性[protocolAttrField]不能为空");
        }

        // 如果是点位协议, 同一款产品下面上/下行的点位地址必须唯一
        if(config.getProtocolAttrField().equals(PointProtocolConfig.POINT_ADDRESS)) {
            CtrlMode ctrlMode = this.getProtocolCtrlModelByProductId(entity.getProductId());
            if (ctrlMode == CtrlMode.POINT) {
                if(config.getValue().startsWith("@")) { //
                    throw new ServiceException("点位地址错误["+config.getValue()+"], 不允许提及模型属性");
                }

                if(this.getBaseMapper().hasSameAddress(config.getId(), entity.getProductId(), entity.getType(), PointProtocolConfig.POINT_ADDRESS, config.getValue())) {
                    throw new ServiceException("同一个产品不允许有相同的点位地址,"+config.getDirection().getDesc()+"点位地址["+config.getValue()+"]已经存在");
                }
            }
        }
        if(StrUtil.isNotBlank(config.getValue())) {
            if(config.getValue().startsWith("@")) {
                if(config.getModelAttrId() == null) {
                    throw new ServiceException("未指定模型属性[modelAttrId]");
                }

                if(StrUtil.isBlank(config.getAttrField())) {
                    config.setAttrField(config.getValue().substring(1));
                }
            }
        }
    }

    @Override
    @Transactional
    public BooleanResult updateById(ModelApi entity) {
        if(this.getBaseMapper().isEnabled(entity.getProductId())) {
            throw new ServiceException("请先停用产品后更新");
        }

        this.getByProductIdAndCode(entity.getProductId(), entity.getCode()).ifPresent(item -> {
           if(!item.getId().equals(entity.getId())) {
               throw new ServiceException("接口代码已存在["+entity.getCode()+"]");
           }
        });

        if(!CollectionUtils.isEmpty(entity.getDownConfig())) {
            entity.getDownConfig().forEach(item -> resolverModelApiConfig(entity, item));
            // 重新添加配置
            this.modelApiConfigService.updateBatchById(entity.getDownConfig());
        }

        if(!CollectionUtils.isEmpty(entity.getUpConfig())) {
            entity.getUpConfig().forEach(item -> resolverModelApiConfig(entity, item));
            this.modelApiConfigService.updateBatchById(entity.getUpConfig());
        }

        return super.updateById(entity);
    }

    @Override
    @Transactional
    public BooleanResult removeByIds(Collection<? extends Serializable> idList) {
        listByIds(idList).stream().filter(item -> item != null).forEach(item -> {
            if(this.getBaseMapper().isEnabled(item.getProductId())) {
                throw new ServiceException("请先停用产品后删除");
            }

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

    @Override
    public CtrlMode getProtocolCtrlModelByProductId(Long productId) {
        return this.getBaseMapper().getProtocolCtrlModelByProductId(productId);
    }

    @Override
    public ListResult<ModelApi> listAsStatusModelApi() {
        return new ListResult<>(this.getBaseMapper().listAsStatusModelApi());
    }

    @Override
    public DetailResult<ModelApi> getAsStatusModelApi(Long productId) {
        return new DetailResult<>(this.getBaseMapper().getAsStatusModelApi(productId));
    }

    @Override
    public DetailResult<ModelApi> detailByCode(String modelApiCode) {
        return new DetailResult<>(this.getBaseMapper().detailByCode(modelApiCode));
    }
}
