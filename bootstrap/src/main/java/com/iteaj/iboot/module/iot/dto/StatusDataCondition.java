package com.iteaj.iboot.module.iot.dto;

import lombok.Data;

import java.util.List;

@Data
public class StatusDataCondition {

    /**
     * 分页时的每页条数
     */
    private Integer size;

    /**
     * 分页时的第几页
     */
    private Integer current;

    /**
     * 产品编码
     */
    private List<String> productCodes;
}
