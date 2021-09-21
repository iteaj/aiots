package com.iteaj.framework.spi.admin;

public class DefaultModule implements Module{

    private int order;
    private String msn;
    private String name;

    protected DefaultModule(int order, String msn, String name) {
        this.order = order;
        this.msn = msn;
        this.name = name;
    }

    /**
     *
     * @param msn 模块编号不能重复
     * @param name
     * @param order
     * @return
     */
    public static Module build(String msn, String name, int order) {
        return new DefaultModule(order, msn, name);
    }

    @Override
    public String getMsn() {
        return this.msn;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getOrder() {
        return this.order;
    }
}
