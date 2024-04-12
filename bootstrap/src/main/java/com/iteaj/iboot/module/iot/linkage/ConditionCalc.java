package com.iteaj.iboot.module.iot.linkage;

public interface ConditionCalc {

    /**
     * 运算符名称
     * @return
     */
    String getName();

    /**
     * 运算符说明
     * @return
     */
    default String getDesc() {
        return getName();
    }

    /**
     * 获取运算符名称
     * @return
     */
    String getOperational();

    /**
     * 计算
     * @param value 当前值
     * @param condition 条件值
     * @return
     */
    default boolean calc(Object value, Object condition) {return false;}
}
