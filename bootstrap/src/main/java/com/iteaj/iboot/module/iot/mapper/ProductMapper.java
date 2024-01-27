package com.iteaj.iboot.module.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.dto.ProductDto;
import com.iteaj.iboot.module.iot.entity.Product;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 设备型号 Mapper 接口
 * </p>
 *
 * @author iteaj
 * @since 2022-07-22
 */
public interface ProductMapper extends BaseMapper<Product> {

    IPage<ProductDto> pageOfDetail(Page<Product> page, Product entity);

    void removeModel(Long productId);

    ProductDto detailById(Long id);

    boolean hasBind(Collection<? extends Serializable> list);

    List<ProductDto> listOfDetail(Product entity);

    ProductDto joinDetailById(Long productId);

    ProductDto debugById(Long productId);
}
