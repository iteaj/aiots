package com.iteaj.admin.service;

import com.iteaj.admin.dao.IRoleDao;
import com.iteaj.admin.dto.RoleDto;
import com.iteaj.admin.entity.Role;
import com.iteaj.util.module.mvc.IBaseService;
import com.iteaj.util.module.mvc.OptionalResult;
import com.iteaj.util.module.mvc.result.DetailResult;

/**
 * create time: 2019/11/27
 *
 * @author iteaj
 * @since 1.0
 */
public interface IRoleService extends IBaseService<IRoleDao, Role> {

    DetailResult<RoleDto> detail(Long id);

    void createRoleAndPerms(RoleDto role);

    void updateRoleAndPermsById(Role role);
}
