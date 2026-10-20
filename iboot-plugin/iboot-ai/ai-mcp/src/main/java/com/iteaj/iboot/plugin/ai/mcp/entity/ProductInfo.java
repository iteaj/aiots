package com.iteaj.iboot.plugin.ai.mcp.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductInfo {

    private String name;
    private String code;
    private String remark;

    private List<DeviceInfo> devices;

    public static record DeviceInfo(String name, String uid) { }
}
