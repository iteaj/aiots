package com.iteaj.iboot.module.iot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.IBaseService;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.iboot.module.iot.dto.ProductDto;
import com.iteaj.iboot.module.iot.dto.ProtocolToProductModel;
import com.iteaj.iboot.module.iot.entity.Product;

/**
 * <p>
 * 设备型号 服务类
 * </p>
 *
 * @author iteaj
 * @since 2022-07-22
 */
public interface IProductService extends IBaseService<Product> {

    PageResult<IPage<ProductDto>> pageOfDetail(Page page, Product entity);

    /**
     * 移除物模型相关的所有配置
     * @see com.iteaj.iboot.module.iot.entity.ModelApi
     * @see com.iteaj.iboot.module.iot.entity.ModelAttr
     * @see com.iteaj.iboot.module.iot.entity.ModelApiConfig
     * @param productId
     */
    void removeModel(Long productId);


    /**
     * 协议模型转产品模型
     * @param protocolId 协议id
     * @return
     */
    ProtocolToProductModel protocolToProductModel(Long protocolId);

    /***
     * 根据产品代码获取产品
     * @param code
     * @return
     */
    DetailResult<Product> getByCode(String code);
}
