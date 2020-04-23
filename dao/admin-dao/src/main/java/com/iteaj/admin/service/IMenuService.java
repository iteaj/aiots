package com.iteaj.admin.service;

import com.iteaj.admin.dao.IMenuDao;
import com.iteaj.admin.entity.Menu;
import com.iteaj.util.Result;
import com.iteaj.util.module.mvc.IBaseService;
import com.iteaj.util.module.mvc.result.ListResult;

import java.util.List;

public interface IMenuService extends IBaseService<IMenuDao, Menu> {

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
     * 查询前端菜单栏所使用的菜单树
     * @param aid 管理员id
     * @param isSuper 是否是超级管理员
     * @return
     */
    ListResult<Menu> selectMenuBarTrees(Long aid, boolean isSuper);

    /**
     * 不包含菜单类型是权限的菜单
     * @param menu
     * @return
     */
    ListResult selectTreesForNoA(Menu menu);

    /**
     * 新增：增、删、改、查权限功能点
     * @param menu
     */
    void createDefaultAuth(Menu menu);

    /**
     * 通过url地址获取菜单
     * @param url
     * @return
     */
    Menu getByUrl(String url);
}
