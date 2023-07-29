package com.iteaj.framework.spi.admin;

import org.springframework.core.Ordered;

/**
 * create time: 2020/7/29
 *  通过此对象可以获取对应的资源信息
 * @author iteaj
 * @since 1.0
 */
public class Module implements Ordered {

    private int order;
    private String msn;
    private String name;

    protected Module(String msn, String name, int order) {
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
        return new Module(msn, name, order);
    }

    public String getMsn() {
        return this.msn;
    }

    public String getName() {
        return this.name;
    }

    public int getOrder() {
        return this.order;
    }
}
