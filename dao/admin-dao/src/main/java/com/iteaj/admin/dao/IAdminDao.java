package com.iteaj.admin.dao;

import com.iteaj.admin.dto.AdminDto;
import com.iteaj.admin.entity.Admin;
import com.iteaj.util.module.mvc.orm.IBaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IAdminDao extends IBaseDao<Admin> {

    void createAdmin(AdminDto adminDto);

    void updateAdmin(AdminDto adminDto);

    AdminDto getAdminDetailById(Long id);

    void updatePwdById(@Param("id") Long id, @Param("password") String password);

    void deleteAllJoinByIds(List<Long> list);
}
