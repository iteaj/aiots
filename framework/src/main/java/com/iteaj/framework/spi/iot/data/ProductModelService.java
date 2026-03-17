package com.iteaj.framework.spi.iot.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 产品物模型相关服务, 包括提供模型属性, 模型接口, 产品信息, 设备信息等数据
 */
public interface ProductModelService {

    /**
     * 获取产品列表
     * @param product 产品条件
     * @return 产品列表
     */
    List<? extends ProductMeta> listProducts(ProductMeta product);

    /**
     *
     * 获取产品模型
     * @param productCode 产品码
     * @return 产品模型
     */
    ProductModel getProductModel(String productCode);

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

    /**
     * 产品模型
     */
    @Getter
    @Setter
    class ProductModel {
        private String name;
        private String code;
        private String remark;
        private String status;

        private List<? extends ModelApiMeta> apis;
        private List<? extends ModelDirectMeta> directs;

        public ProductModel(String name, String code, String remark, String status
                , List<? extends ModelDirectMeta> directs, List<? extends ModelApiMeta> apis) {
            this.name = name;
            this.code = code;
            this.remark = remark;
            this.status = status;
            this.directs = directs;
            this.apis = apis;
        }
    }
}
