package com.iteaj.framework.spi.admin;

public enum MenuType {
    A("功能"), M("目录"), V("菜单");

    public String desc;

    MenuType(String desc) {
        this.desc = desc;
    }
}
