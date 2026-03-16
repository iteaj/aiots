package com.iteaj.framework.spi.iot.data;

import java.util.List;

/**
 * 产品物模型相关服务, 包括提供模型属性, 模型接口, 产品信息, 设备信息等数据
 */
public interface ProductModelService {

    /**
     * 获取产品列表
     * @return 产品列表
     */
    List<? extends ProductMeta> listProducts();

    /**
     * 获取指定产品的指令码集合
     * @param productCode 产品码
     * @return 指令码列表
     */
    List<? extends ModelDirectMeta> listDirectMeta(String productCode);

    /**
     * 获取指定产品的模型接口集合
     * @param productCode 产品码
     * @return 模型接口列表
     */
    List<? extends ModelApiMeta> listModelApiMeta(String productCode);

}
