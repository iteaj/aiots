package com.iteaj.iboot.module.iot.service;

import com.iteaj.framework.result.ListResult;
import com.iteaj.iboot.module.iot.entity.ProductType;
import com.iteaj.framework.IBaseService;

/**
 * <p>
 * 设备类型 服务类
 * </p>
 *
 * @author iteaj
 * @since 2022-05-15
 */
public interface IProductTypeService extends IBaseService<ProductType> {

    /**
     * 获取树形结构列表
     * @return
     */
    ListResult<ProductType> tree(ProductType entity);
}
