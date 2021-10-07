package com.iteaj.msn.core.service;

import com.iteaj.framework.IBaseService;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.spi.admin.Module;
import com.iteaj.msn.core.entity.Menu;

import java.util.List;

public interface IMenuService extends IBaseService<Menu> {

    /**
     * 查找超级管理员在指定模块下的所有菜单
     * @param msn
     * @return
     */
    List<Menu> selectBySuperAndMsn(String msn);

    /**
     * 查询菜单树
     * 默认不对状态status作为查询条件
     * @param menu
     * @return
     */
    ListResult<Menu> selectMenuTrees(Menu menu);

    /**
     * 查找指定模块下面的菜单
     * @param menu
     * @param modules
     * @return
     */
    ListResult<Menu> selectMenuTreesInModules(Menu menu, List<Module> modules);

    /**
     * 查询前端菜单栏所使用的菜单树
     * @param aid 管理员id
     * @param isSuper 是否是超级管理员
     * @return
     */
    ListResult<Menu> selectMenuBarTrees(Long aid,  boolean isSuper);

    /**
     * 查询前端菜单栏所使用的菜单树
     * @param aid 管理员id
     * @param modules 要查找的模块
     * @param isSuper 是否是超级管理员
     * @return
     */
    ListResult<Menu> selectMenuBarTreesInModules(Long aid, List<Module> modules, boolean isSuper);

    /**
     * 不包含菜单类型是权限的菜单
     * @return
     */
    ListResult<Menu> selectParentTrees();

    /**
     * 通过url地址获取菜单
     * @param url
     * @return
     */
    Menu getByUrl(String url);
}
