package com.iteaj.iboot.module.iot.linkage.calc;

import com.iteaj.iboot.module.iot.linkage.ConditionCalc;

public class GTConditionCalc implements ConditionCalc {
    @Override
    public String getName() {
        return "大于";
    }

    @Override
    public String getOperational() {
        return "gt";
    }
}
