package com.iteaj.framework.spi.admin;

public enum MenuType {
    A("权限"), M("目录"), V("视图");

    public String desc;

    MenuType(String desc) {
        this.desc = desc;
    }
}
