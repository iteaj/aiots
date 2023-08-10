package com.iteaj.framework.spi.admin;

import org.springframework.core.Ordered;

/**
 * create time: 2020/7/29
 *  通过此对象可以获取对应的资源信息
 * @author iteaj
 * @since 1.0
 */
public class Module implements Ordered {

    /**
     * 排序值
     */
    private int order;

    /**
     * 模块标识, 用于菜单的msn
     */
    private String msn;

    /**
     * 模块名称
     */
    private String name;

    /**
     * 是模块还是插件
     */
    private boolean plugin;

    /**
     * 表前缀
     */
    private String tablePrefix;

    /**
     * 模块包名
     */
    private String packageName;

    protected Module(String msn, String name, int order) {
        this(msn, name, order, msn + "_", false);
    }

    protected Module(String msn, String name, int order, String packageName, boolean plugin) {
        this(msn, name, order, msn + "_", packageName, plugin);
    }

    protected Module(String msn, String name, int order, String tablePrefix, String packageName, boolean plugin) {
        this.order = order;
        this.msn = msn;
        this.name = name;
        this.plugin = plugin;
        this.tablePrefix = tablePrefix;
        this.packageName = packageName;
    }

    /**
     *
     * @param msn 模块编号不能重复
     * @param name
     * @param order
     * @return
     */
    public static Module module(String msn, String name, int order) {
        return new Module(msn, name, order);
    }

    /**
     * @param msn 模块编号不能重复
     * @param name
     * @param order
     * @return
     */
    public static Module module(String msn, String name, int order, String packageName) {
        return new Module(msn, name, order, packageName, false);
    }

    /**
     * @param msn 模块编号不能重复
     * @param name
     * @param order
     * @return
     */
    public static Module module(String msn, String name, int order, String tablePrefix, String packageName) {
        return new Module(msn, name, order, tablePrefix, packageName, false);
    }

    public boolean isPlugin() {
        return plugin;
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

    public String getTablePrefix() {
        return tablePrefix;
    }

    public String getPackageName() {
        return packageName;
    }
}
