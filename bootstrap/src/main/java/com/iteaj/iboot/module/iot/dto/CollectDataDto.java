package com.iteaj.iboot.module.iot.dto;

import com.iteaj.iboot.module.iot.entity.CollectData;
import lombok.Data;

@Data
public class CollectDataDto extends CollectData {

    /**
     * 产品类型id
     */
    private Long productTypeId;

    /**
     * 产品id
     */
    private Long productId;

}
