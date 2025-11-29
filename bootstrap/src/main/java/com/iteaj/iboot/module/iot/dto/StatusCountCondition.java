package com.iteaj.iboot.module.iot.dto;

import lombok.Data;

import java.util.List;

@Data
public class StatusCountCondition {

    /**
     * 产品编码
     */
    private List<String> productCodes;
}
