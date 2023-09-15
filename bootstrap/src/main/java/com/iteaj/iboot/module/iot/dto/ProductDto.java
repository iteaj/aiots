package com.iteaj.iboot.module.iot.dto;

import com.iteaj.iboot.module.iot.entity.Product;
import lombok.Data;

@Data
public class ProductDto extends Product {

    /**
     * 设备数量
     */
    private Integer deviceCount;
}
