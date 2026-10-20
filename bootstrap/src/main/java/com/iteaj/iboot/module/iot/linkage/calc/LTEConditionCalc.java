package com.iteaj.iboot.module.iot.linkage.calc;

import com.iteaj.iboot.module.iot.linkage.ConditionCalc;

import java.util.Date;

public class LTEConditionCalc implements ConditionCalc {

    @Override
    public String getName() {
        return "小于等于";
    }

    @Override
    public String getOperational() {
        return "lte";
    }

    @Override
    public boolean calc(Object value, Object condition) {
        Date date = resolverDate(value, condition);
        if(date != null) {
            return ((Date) value).getTime() <= date.getTime();
        }
        return Double.valueOf(value.toString()) <= Double.valueOf(condition.toString());
    }
}
