package com.iteaj.msn.core.service;

import com.iteaj.msn.core.mapper.IRoleDao;
import com.iteaj.msn.core.dto.RoleDto;
import com.iteaj.msn.core.entity.Role;
import com.iteaj.framework.IBaseService;
import com.iteaj.framework.result.DetailResult;

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

    void updateRolePermsById(Role role);

    List<Long> selectByAdminId(Serializable id);
}
