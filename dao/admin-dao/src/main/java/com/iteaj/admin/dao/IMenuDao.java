package com.iteaj.admin.dao;

import com.iteaj.admin.entity.Menu;
import com.iteaj.util.module.mvc.orm.IBaseDao;

import java.util.List;

public interface IMenuDao extends IBaseDao<Menu> {

    /**
     * 查找指定管理员所拥有的菜单权限
     * @param aid
     * @return
     */
    List<String> selectAdminMenus(Long aid);
}
