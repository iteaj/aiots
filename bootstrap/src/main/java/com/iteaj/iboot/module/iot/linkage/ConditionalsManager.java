package com.iteaj.iboot.module.iot.linkage;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.IVOption;
import com.iteaj.iboot.module.iot.linkage.calc.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConditionalsManager {

    private static Map<String, ConditionCalc> calcMap = new HashMap<>();
    private static ConditionalsManager instance = new ConditionalsManager();

    static {
        register(new EQConditionCalc());
        register(new GTEConditionCalc());
        register(new GTConditionCalc());
        register(new LTEConditionCalc());
        register(new LTConditionCalc());
        register(new NEQConditionCalc());
        register(new INConditionCalc());
        register(new ContainConditionCalc());
        register(new BetweenConditionCalc());
    }

    public static ConditionalsManager build(List<ConditionCalc> calcs) {
        if(CollectionUtil.isNotEmpty(calcs)) {
            calcs.forEach(item -> register(item));
        }

        return instance;
    }

    public static ConditionCalc register(ConditionCalc calc) {
        if(!calcMap.containsKey(calc.getOperational())) {
            return calcMap.put(calc.getOperational(), calc);
        }

        return calc;
    }

    public static ConditionCalc get(String operational) {
        return calcMap.get(operational);
    }

    public static List<IVOption> options() {
        return calcMap.values().stream()
                .map(item -> new IVOption(item.getName(), item.getOperational(), item.getDesc()))
                .collect(Collectors.toList());
    }
}
