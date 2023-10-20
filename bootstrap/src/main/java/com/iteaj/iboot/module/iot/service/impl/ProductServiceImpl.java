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
import com.iteaj.iboot.module.iot.dto.ProductDto;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iboot.module.iot.entity.ModelAttr;
import com.iteaj.iboot.module.iot.entity.Product;
import com.iteaj.iboot.module.iot.mapper.ProductMapper;
import com.iteaj.iboot.module.iot.service.IModelApiService;
import com.iteaj.iboot.module.iot.service.IModelAttrService;
import com.iteaj.iboot.module.iot.service.IProductService;
import com.iteaj.iboot.module.iot.service.IProtocolService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
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

    public ProductServiceImpl(IProtocolService protocolService
            , IModelApiService modelApiService, IModelAttrService modelAttrService) {
        this.protocolService = protocolService;
        this.modelApiService = modelApiService;
        this.modelAttrService = modelAttrService;
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
        protocolService.getById(entity.getProtocolId()).ifPresent(protocol -> {
            DeviceProtocolSupplier supplier = ProtocolSupplierManager.get(protocol.getCode());
            if(supplier == null) {
                throw new ServiceException("没有找到对应的协议["+protocol.getCode()+"]");
            }

            // 保存协议使用的属性
            List<ModelAttr> modelAttrs = supplier.getProtocol().getAttrs().values().stream().map(attr -> {
                ModelAttr modelAttr = new ModelAttr();
                BeanUtils.copyProperties(attr, modelAttr);
                modelAttr.setProductId(entity.getId());
                return modelAttr;
            }).collect(Collectors.toList());

            if(!CollectionUtils.isEmpty(modelAttrs)) {
                modelAttrService.saveBatch(modelAttrs);
            }

            // 保存协议所有api
            List<ModelApi> modelApis = supplier.getProtocol().getApis().values().stream().map(api -> {
                ModelApi modelApi = new ModelApi();
                BeanUtils.copyProperties(api, modelApi);
                modelApi.setProductId(entity.getId())
                        .setType("protocol")
                        .setStatus("enabled");
                return modelApi;
            }).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(modelApis)) {
                modelApiService.saveBatch(modelApis);
            }
        });
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
}
