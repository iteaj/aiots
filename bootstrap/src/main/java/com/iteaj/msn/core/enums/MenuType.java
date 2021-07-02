package com.iteaj.msn.core.enums;

public enum MenuType {
    A("权限"), M("目录"), V("视图");

    public String desc;

    MenuType(String desc) {
        this.desc = desc;
    }
}
