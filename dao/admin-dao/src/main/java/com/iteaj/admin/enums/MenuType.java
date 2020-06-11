package com.iteaj.admin.enums;

public enum MenuType {
    A("权限"), M("菜单"), G("菜单组"), V("视图");

    public String desc;
    MenuType(String desc) {
        this.desc = desc;
    }
}
