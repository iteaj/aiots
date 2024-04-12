package com.iteaj.iboot.module.iot.linkage.calc;

import com.iteaj.iboot.module.iot.linkage.ConditionCalc;

public class LTConditionCalc implements ConditionCalc {
    @Override
    public String getName() {
        return "小于";
    }

    @Override
    public String getOperational() {
        return "lt";
    }
}
