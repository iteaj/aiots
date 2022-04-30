package com.iteaj.iboot.msn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iteaj.iboot.msn.core.dto.AdminDto;
import com.iteaj.iboot.msn.core.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

@Mapper
public interface IAdminDao extends BaseMapper<Admin> {

    void createAdmin(AdminDto adminDto);

    void updateAdmin(AdminDto adminDto);

    AdminDto getAdminDetailById(Long id);

    void updatePwdById(@Param("id") Long id, @Param("password") String password);

    void deleteAllJoinByIds(List<Long> list);

    List<String> selectPermsById(Serializable id);

    AdminDto getAdminCenter(Serializable id);

    String getAdminPassword(Long id);
}
