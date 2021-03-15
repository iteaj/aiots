package com.iteaj.core.service;

import com.iteaj.core.mapper.IAdminDao;
import com.iteaj.core.dto.AdminDto;
import com.iteaj.core.entity.Admin;
import com.iteaj.util.module.mvc.IBaseService;
import com.iteaj.util.module.mvc.result.DetailResult;

import java.io.Serializable;
import java.util.List;

public interface IAdminService extends IBaseService<IAdminDao, Admin> {

    /**
     * 通过用户名获取账号
     * @param username
     * @return
     */
    Admin getByAccount(String username);

    void createAdmin(AdminDto adminDto);

    /**
     * 更新管理员
     * @param adminDto
     */
    void updateAdminAndRole(AdminDto adminDto);

    DetailResult<AdminDto> getAdminDetailById(Long id);

    /**
     * 更新密码
     */
    void updatePwdById(Long id, String password);

    void deleteAllJoinByIds(List<Long> list);

    List<String> selectPermsById(Serializable id);

    void updateCurrentUserInfo(Admin admin);
}
