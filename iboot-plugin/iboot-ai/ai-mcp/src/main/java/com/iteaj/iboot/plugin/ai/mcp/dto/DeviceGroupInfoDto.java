package com.iteaj.iboot.plugin.ai.mcp.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DeviceGroupInfoDto {

    /**
     * 分组名称
     */
    private String name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 分组地址
     */
    private String address;

    /**
     * 分组记录ID
     */
    private String id;
}
