package com.iteaj.iboot.module.iot.linkage.calc;

import com.alibaba.fastjson.JSONArray;
import com.iteaj.iboot.module.iot.linkage.ConditionCalc;

import java.util.Set;
import java.util.stream.Collectors;

public class INConditionCalc implements ConditionCalc {

    @Override
    public String getName() {
        return "某项之中";
    }

    @Override
    public String getOperational() {
        return "in";
    }

    @Override
    public String getDesc() {
        return "([x,...])";
    }

    @Override
    public boolean calc(Object value, Object condition) {
        JSONArray array = JSONArray.parseArray(condition.toString());
        Set<String> collect = array.stream().map(item -> String.valueOf(item)).collect(Collectors.toSet());
        return collect.contains(value.toString());
    }
}
