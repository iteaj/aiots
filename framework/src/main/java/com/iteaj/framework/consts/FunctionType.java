package com.iteaj.framework.consts;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 默认的功能点
 */
public enum FunctionType {
    View("浏览"),
    Query("查询"),
    Add("新增"),
    Del("删除"),
    Edit("更新"),
    Import("导入"),
    Export("导出");

    @EnumValue // 表示存入数据库的值
    private final String desc;

    FunctionType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
