package com.iteaj.iboot.module.iot.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.iboot.module.iot.dto.ProductDto;
import com.iteaj.iboot.module.iot.entity.Product;
import com.iteaj.iboot.module.iot.mapper.ProductMapper;
import com.iteaj.iboot.module.iot.service.IProductService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;

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

    @Override
    public BooleanResult save(Product entity) {
        this.getOne(Wrappers.<Product>lambdaQuery()
                .eq(Product::getProductTypeId, entity.getProductTypeId())
                .eq(Product::getName, entity.getName()))
                .ofNullable().ifPresent(one -> {throw new ServiceException("此产品已经存在["+entity.getName()+"]");});

        return super.save(entity);
    }

    @Override
    public BooleanResult updateById(Product entity) {
        this.getOne(Wrappers.<Product>lambdaQuery()
                .ne(Product::getId, entity.getId())
                .eq(Product::getProductTypeId, entity.getProductTypeId())
                .eq(Product::getName, entity.getName()))
                .ofNullable().ifPresent(one -> {throw new ServiceException("此产品已经存在["+entity.getName()+"]");});

        return super.updateById(entity);
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
    public PageResult<IPage<ProductDto>> detailOfPage(Page page, Product entity) {
        return new PageResult<>(this.getBaseMapper().detailOfPage(page, entity));
    }
}
