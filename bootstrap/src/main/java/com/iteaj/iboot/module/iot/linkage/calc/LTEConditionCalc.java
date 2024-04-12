package com.iteaj.iboot.module.iot.linkage.calc;

import com.iteaj.iboot.module.iot.linkage.ConditionCalc;

public class LTEConditionCalc implements ConditionCalc {

    @Override
    public String getName() {
        return "小于等于";
    }

    @Override
    public String getOperational() {
        return "lte";
    }
}
