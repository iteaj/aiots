package com.iteaj.admin.dao;

import com.iteaj.admin.dto.RoleDto;
import com.iteaj.admin.entity.Role;
import com.iteaj.util.module.mvc.orm.IBaseDao;

import java.util.List;

/**
 * create time: 2019/11/27
 *
 * @author iteaj
 * @since 1.0
 */
public interface IRoleDao extends IBaseDao<Role> {

    RoleDto joinRoleMenuById(Long id);

    void updateRoleAndPermsById(Role role);

    void createRoleAndPerms(RoleDto role);

    void deleteAllJoinByIds(List<Long> list);
}
