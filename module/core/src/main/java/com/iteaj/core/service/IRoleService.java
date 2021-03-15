package com.iteaj.core.service;

import com.iteaj.core.mapper.IRoleDao;
import com.iteaj.core.dto.RoleDto;
import com.iteaj.core.entity.Role;
import com.iteaj.util.module.mvc.IBaseService;
import com.iteaj.util.module.mvc.result.DetailResult;

import java.io.Serializable;
import java.util.List;

/**
 * create time: 2019/11/27
 *
 * @author iteaj
 * @since 1.0
 */
public interface IRoleService extends IBaseService<IRoleDao, Role> {

    void deleteAllJoinByIds(List<Long> list);

    DetailResult<RoleDto> detail(Long id);

    void createRoleAndPerms(RoleDto role);

    void updateRoleAndPermsById(Role role);

    List<Long> selectByAdminId(Serializable id);
}
