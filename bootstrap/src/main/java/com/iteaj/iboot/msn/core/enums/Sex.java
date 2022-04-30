package com.iteaj.iboot.msn.core.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

public enum Sex implements IEnum<String> {
    man, woman, non;

    @Override
    public String getValue() {
        return this.name();
    }
}
