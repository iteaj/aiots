package com.iteaj.iboot.module.iot.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.spi.iot.DeviceProtocolSupplier;
import com.iteaj.framework.spi.iot.ProtocolSupplierManager;
import com.iteaj.framework.spi.iot.consts.ProtocolImplMode;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelAttr;
import com.iteaj.iboot.module.iot.consts.AttrOrigin;
import com.iteaj.iboot.module.iot.dto.ProductDto;
import com.iteaj.iboot.module.iot.dto.ProtocolToProductModel;
import com.iteaj.iboot.module.iot.entity.*;
import com.iteaj.iboot.module.iot.mapper.ProductMapper;
import com.iteaj.iboot.module.iot.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 设备型号 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2022-07-22
 */
@Service
public class ProductServiceImpl extends BaseServiceImpl<ProductMapper, Product> implements IProductService {

    private final IProtocolService protocolService;
    private final IModelApiService modelApiService;
    private final IModelAttrService modelAttrService;
    private final IModelApiConfigService modelApiConfigService;

    public ProductServiceImpl(IProtocolService protocolService
            , IModelApiService modelApiService, IModelAttrService modelAttrService
            , IModelApiConfigService modelApiConfigService) {
        this.protocolService = protocolService;
        this.modelApiService = modelApiService;
        this.modelAttrService = modelAttrService;
        this.modelApiConfigService = modelApiConfigService;
    }

    @Override
    @Transactional
    public BooleanResult save(Product entity) {
        this.getOne(Wrappers.<Product>lambdaQuery()
                .eq(Product::getProductTypeId, entity.getProductTypeId())
                .eq(Product::getName, entity.getName()))
                .ifPresent(one -> {throw new ServiceException("此产品已经存在["+entity.getName()+"]");});

        BooleanResult save = super.save(entity);
        // 保存协议对应的指令到物模型
        saveProtocolToModel(entity);

        return save;
    }

    @Override
    @Transactional
    public BooleanResult updateById(Product entity) {
        this.getOne(Wrappers.<Product>lambdaQuery()
                .ne(Product::getId, entity.getId())
                .eq(Product::getProductTypeId, entity.getProductTypeId())
                .eq(Product::getName, entity.getName()))
                .ofNullable().ifPresent(one -> {throw new ServiceException("此产品已经存在["+entity.getName()+"]");});

        // 如果修改了协议需要删除所有物模型数据
        this.getById(entity.getId()).ifPresent(product -> {
            if(!product.getProtocolId().equals(entity.getProtocolId())) {
                // 移除所有物模型信息
                this.removeModel(entity.getId());

                // 保存协议对应的指令到物模型
                saveProtocolToModel(entity);
            }
        });


        return super.updateById(entity);
    }

    private void saveProtocolToModel(Product entity) {
        ProtocolToProductModel productModel = protocolToProductModel(entity.getProtocolId());
        // 批量保存物模型属性
        if(!CollectionUtils.isEmpty(productModel.getAttrs())) {
            productModel.getAttrs().forEach(item -> item.setProductId(entity.getId()));
            modelAttrService.saveBatch(productModel.getAttrs());
        }

        // 批量保存事件接口
        List<ModelApi> apis = new ArrayList<>();
        if(!CollectionUtils.isEmpty(productModel.getEventApis())) {
            apis.addAll(productModel.getEventApis());
        }

        if(!CollectionUtils.isEmpty(productModel.getProtocolApis())) {
            productModel.getProtocolApis().forEach(item -> item.setProductId(entity.getId()));
            apis.addAll(productModel.getProtocolApis());
        }

        if(!CollectionUtils.isEmpty(apis)) {
            apis.forEach(item -> {
                item.setProductId(entity.getId());
                modelApiService.save(item);
                List<ModelApiConfig> apiConfigs = new ArrayList<>();

                if(!CollectionUtils.isEmpty(item.getUpConfig())) {
                    apiConfigs.addAll(item.getUpConfig());
                }

                if(!CollectionUtils.isEmpty(item.getDownConfig())) {
                    apiConfigs.addAll(item.getDownConfig());
                }

                // 批量保存api配置
                if(!CollectionUtils.isEmpty(apiConfigs)) {
                    apiConfigs.forEach(config -> config.setProductId(entity.getId()));
                    modelApiConfigService.saveBatch(apiConfigs);
                }
            });
        }
    }

    @Override
    public BooleanResult removeByIds(Collection<? extends Serializable> idList) {
        if(CollectionUtils.isEmpty(idList)) {
            return new BooleanResult(false);
        }
        if(idList.size() > 1) {
            return new BooleanResult(false).fail("不支持批量删除");
        }

        return super.removeByIds(idList);
    }

    @Override
    public PageResult<IPage<ProductDto>> pageOfDetail(Page page, Product entity) {
        return new PageResult<>(this.getBaseMapper().pageOfDetail(page, entity));
    }

    @Override
    public void removeModel(Long productId) {
        this.getBaseMapper().removeModel(productId);
    }

    @Override
    public ProtocolToProductModel protocolToProductModel(Long protocolId) {
        ProtocolToProductModel model = new ProtocolToProductModel();
        this.protocolService.getByDetail(protocolId).ifPresent(item -> {
            if(item.getImplMode() != ProtocolImplMode.Custom) {
                DeviceProtocolSupplier supplier = ProtocolSupplierManager.get(item.getCode());
                if(supplier != null) {
                    ProtocolModel protocol = supplier.getProtocol();

                    // 协议接口
                    protocol.getApis().values().stream().forEach(api -> {
                        // 协议接口
                        ModelApi modelApi = new ModelApi();
                        BeanUtils.copyProperties(api, modelApi);

                        // 协议接口上行配置
                        List<ModelApiConfig> upConfig = api.getUpConfig()
                                .values().stream().map(config -> {
                                    ProtocolModelAttr attr = protocol.getAttrs().get(config.getProtocolModelAttrField());
                                    // 将协议模型的api配置转换成协议api配置
                                    return convertProtocolApiConfig(modelApi, config, attr);
                                }).collect(Collectors.toList());

                        // 协议接口下行配置
                        List<ModelApiConfig> downConfig = api.getDownConfig()
                                .values().stream().map(config -> {
                                    ProtocolModelAttr attr = protocol.getAttrs().get(config.getProtocolModelAttrField());
                                    // 将协议模型的api配置转换成协议api配置
                                    return convertProtocolApiConfig(modelApi, config, attr);
                                }).collect(Collectors.toList());

                        if(api.getType() == ProtocolApiType.event) {
                            model.getEventApis().add(modelApi.setUpConfig(upConfig).setDownConfig(downConfig));
                        } else if(api.getType() == ProtocolApiType.func) {
                            modelApi.setType(ProtocolApiType.protocol); //协议的功能将作为物模型的 协议指令
                            model.getProtocolApis().add(modelApi.setUpConfig(upConfig).setDownConfig(downConfig));
                        }
                    });

                    // 协议属性转模型属性
                    List<ModelAttr> attrs = protocol.getAttrs().values().stream()
                        .map(attr -> new ModelAttr()
                                .setName(attr.getName())
                                .setField(attr.getField())
                                .setRemark(attr.getRemark())
                                .setType(attr.getType())
                                .setOrigin(AttrOrigin.Protocol))
                        .collect(Collectors.toList());
                    model.setAttrs(attrs);
                }
            } else {
                List<ModelAttr> attrList = item.getAttrs().stream().map(attr -> {
                    ModelAttr modelAttr = new ModelAttr();
                    BeanUtils.copyProperties(attr, modelAttr);
                    return modelAttr.setOrigin(AttrOrigin.Protocol);
                }).collect(Collectors.toList());

                List<ModelApi> funcApis = item.getFuncApis().stream()
                        .map(api -> convertProtocolApiToModelApi(api))
                        .collect(Collectors.toList());
                List<ModelApi> eventApis = item.getEventApis().stream()
                        .map(api -> convertProtocolApiToModelApi(api))
                        .collect(Collectors.toList());

                model.setProtocolApis(funcApis).setEventApis(eventApis).setAttrs(attrList);
            }
        });

        return model;
    }

    private ModelApi convertProtocolApiToModelApi(ProtocolApi api) {
        ModelApi modelApi = new ModelApi();
        BeanUtils.copyProperties(api, modelApi);

        // 协议的功能转成协议指令
        if(api.getType() == ProtocolApiType.func) {
            modelApi.setType(ProtocolApiType.protocol);
        }

        List<ModelApiConfig> upConfigs = api.getUpConfigs().stream().map(config -> {
            ModelApiConfig apiConfig = new ModelApiConfig();
            BeanUtils.copyProperties(config, apiConfig);
            return apiConfig;
        }).collect(Collectors.toList());

        List<ModelApiConfig> downConfigs = api.getDownConfigs().stream().map(config -> {
            ModelApiConfig apiConfig = new ModelApiConfig();
            BeanUtils.copyProperties(config, apiConfig);
            return apiConfig;
        }).collect(Collectors.toList());

        return modelApi.setUpConfig(upConfigs).setDownConfig(downConfigs);
    }

    private ModelApiConfig convertProtocolApiConfig(ModelApi protocolApi, ProtocolModelApiConfig config, ProtocolModelAttr attr) {
        return new ModelApiConfig()
                .setSort(config.getSort())
                .setLength(config.getLength())
                .setRemark(config.getRemark())
                .setAttrField(attr.getField())
                .setApiCode(protocolApi.getCode())
                .setPosition(config.getPosition())
                .setDirection(config.getDirection());
    }
}
