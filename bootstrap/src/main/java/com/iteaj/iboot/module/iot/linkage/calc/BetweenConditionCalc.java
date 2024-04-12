package com.iteaj.iboot.module.iot.linkage.calc;

import com.iteaj.iboot.module.iot.linkage.ConditionCalc;

public class BetweenConditionCalc implements ConditionCalc {

    @Override
    public String getName() {
        return "两者之间([0-3])";
    }

    @Override
    public String getOperational() {
        return "between";
    }
}
