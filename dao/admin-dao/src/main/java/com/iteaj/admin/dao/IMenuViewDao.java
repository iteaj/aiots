package com.iteaj.admin.dao;

import com.iteaj.admin.entity.Menu;
import com.iteaj.admin.entity.MenuView;
import com.iteaj.util.module.mvc.orm.IBaseDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMenuViewDao extends IBaseDao<MenuView> {

    /**
     * 查找指定用户和指定模块下的菜单
     * @param aid
     * @param msn
     * @return
     */
    List<Menu> selectByAdminAndMsn(@Param("aid") Long aid, @Param("msn") String msn);

}
