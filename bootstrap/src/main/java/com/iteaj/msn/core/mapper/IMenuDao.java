package com.iteaj.msn.core.mapper;

import com.iteaj.msn.core.entity.Menu;
import com.iteaj.util.module.mvc.orm.IBaseDao;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IMenuDao extends IBaseDao<Menu> {

    /**
     * 查找指定管理员所拥有的菜单权限
     * @param aid
     * @return
     */
    List<String> selectAdminMenus(Long aid);
}
