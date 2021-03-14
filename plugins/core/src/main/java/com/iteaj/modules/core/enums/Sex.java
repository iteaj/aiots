package com.iteaj.modules.core.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

public enum Sex implements IEnum<String> {
    男, 女, 未知;

    @Override
    public String getValue() {
        return this.name();
    }
}
