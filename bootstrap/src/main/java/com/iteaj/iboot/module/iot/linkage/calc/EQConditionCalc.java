package com.iteaj.iboot.module.iot.linkage.calc;

import com.iteaj.iboot.module.iot.linkage.ConditionCalc;

public class EQConditionCalc implements ConditionCalc {

    @Override
    public String getName() {
        return "相等";
    }

    @Override
    public String getOperational() {
        return "eq";
    }

}
