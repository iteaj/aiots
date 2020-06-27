package com.iteaj.core.mapper;

import com.iteaj.core.dto.AdminDto;
import com.iteaj.core.entity.Admin;
import com.iteaj.util.module.mvc.orm.IBaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

@Mapper
public interface IAdminDao extends IBaseDao<Admin> {

    void createAdmin(AdminDto adminDto);

    void updateAdmin(AdminDto adminDto);

    AdminDto getAdminDetailById(Long id);

    void updatePwdById(@Param("id") Long id, @Param("password") String password);

    void deleteAllJoinByIds(List<Long> list);

    List<String> selectPermsById(Serializable id);
}
