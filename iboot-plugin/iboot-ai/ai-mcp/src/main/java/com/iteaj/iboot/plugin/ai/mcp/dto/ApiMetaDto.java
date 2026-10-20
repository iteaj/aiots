package com.iteaj.iboot.plugin.ai.mcp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiMetaDto {

    /**
     * 接口地址
     */
    private String url;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 接口说明
     */
    private String remark;

    public ApiMetaDto() {}

    public ApiMetaDto(String url, String name, String method, String remark) {
        this.url = url;
        this.name = name;
        this.method = method;
        this.remark = remark;
    }
}
