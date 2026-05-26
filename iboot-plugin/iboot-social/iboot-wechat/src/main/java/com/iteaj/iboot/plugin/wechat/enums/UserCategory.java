package com.iteaj.iboot.plugin.wechat.enums;

import lombok.Getter;

/**
 * 用户类别：1-管理端 2-会员端
 * @author iteaj
 */
@Getter
public enum UserCategory {

    ADMIN(1, "管理端"),
    MEMBER(2, "会员端");

    private final int value;
    private final String desc;

    UserCategory(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static UserCategory fromValue(Integer value) {
        if (value == null) return null;
        for (UserCategory c : values()) {
            if (c.value == value) return c;
        }
        throw new IllegalArgumentException("未知用户类别: " + value);
    }
}
