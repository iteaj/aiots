package com.iteaj.framework.spi.admin;

/**
 * create time: 2020/7/29
 *
 * @author iteaj
 * @since 1.0
 */
public interface MenuModule extends Module {

    /**
     * 挂载到那个模块菜单下
     * @return
     */
    MenuModule mount();
}
