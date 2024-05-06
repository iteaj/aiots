package com.iteaj.iboot.module.iot.linkage.calc;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.iteaj.iboot.module.iot.linkage.ConditionCalc;

import java.util.Date;
import java.util.Objects;

public class EQConditionCalc implements ConditionCalc {

    @Override
    public String getName() {
        return "等于";
    }

    @Override
    public String getOperational() {
        return "eq";
    }

    @Override
    public boolean calc(Object value, Object condition) {
        Date date = resolverDate(value, condition);
        if(date != null) {
            return ((Date) value).getTime() != date.getTime();
        }

        return Objects.equals(value.toString(), condition.toString());
    }

}
