package com.iteaj.iboot.module.iot.linkage.calc;

import com.iteaj.iboot.module.iot.linkage.ConditionCalc;

import java.util.Date;

public class GTConditionCalc implements ConditionCalc {
    @Override
    public String getName() {
        return "大于";
    }

    @Override
    public String getOperational() {
        return "gt";
    }

    @Override
    public boolean calc(Object value, Object condition) {
        Date date = resolverDate(value, condition);
        if(date != null) {
            return ((Date) value).getTime() > date.getTime();
        }

        return Double.valueOf(value.toString()) > Double.valueOf(condition.toString());
    }
}
