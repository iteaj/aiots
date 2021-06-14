package com.iteaj.msn.core.enums;

/**
 * create time: 2019/12/4
 *
 * @author iteaj
 * @since 1.0
 */
public enum ConfigType {
    def("默认"), sys("系统");

    public String val;

    ConfigType(String val) {
        this.val = val;
    }
}
