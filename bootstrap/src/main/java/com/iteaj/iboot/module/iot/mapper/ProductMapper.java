package com.iteaj.iboot.module.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.dto.ProductDto;
import com.iteaj.iboot.module.iot.entity.Product;

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
}
