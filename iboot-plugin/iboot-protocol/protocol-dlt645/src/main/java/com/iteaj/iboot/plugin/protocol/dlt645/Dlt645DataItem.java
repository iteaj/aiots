package com.iteaj.iboot.plugin.protocol.dlt645;

public enum Dlt645DataItem {

    V("电压", 0.1),
    A("电流", 0.001),
    P("功率", 0.0001),
    PF("功率因素", 0.001),
    IN("相角", 0.1),
    VWD("电压波形失真度", 0.01),
    AWD("电流波形失真度", 0.01),
    VHC("电压谐波含量", 0.01),
    AHC("电流谐波含量", 0.01),
    ;

    private String desc;
    private double resolution;
    private static Dlt645DataItem[] items = new Dlt645DataItem[]{V, A, P, P, P, PF, IN, VWD, AWD, VHC, AHC};

    Dlt645DataItem(String desc, double resolution) {
        this.desc = desc;
        this.resolution = resolution;
    }

    public String getDesc() {
        return desc;
    }

    public double getResolution() {
        return resolution;
    }

    public static Dlt645DataItem valueOf(int item) {
        if(item <= 16) {
            return items[item - 1];
        }

        return null;
    }
}
