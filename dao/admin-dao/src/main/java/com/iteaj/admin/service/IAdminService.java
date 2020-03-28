package com.iteaj.admin.service;

import com.iteaj.admin.dao.IAdminDao;
import com.iteaj.admin.dto.AdminDto;
import com.iteaj.admin.entity.Admin;
import com.iteaj.util.module.mvc.IBaseService;
import com.iteaj.util.module.mvc.result.DetailResult;

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
}
