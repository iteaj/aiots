package com.iteaj.core.mapper;

import com.iteaj.core.entity.Menu;
import com.iteaj.core.entity.MenuView;
import com.iteaj.util.module.mvc.orm.IBaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface IMenuViewDao extends IBaseDao<MenuView> {

    /**
     * 查找指定用户和指定模块下的菜单
     * @param aid
     * @param msn
     * @return
     */
    List<Menu> selectByAdminAndMsn(@Param("aid") Long aid, @Param("msn") String msn);

}
