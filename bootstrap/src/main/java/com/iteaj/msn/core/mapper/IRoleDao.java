package com.iteaj.msn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iteaj.msn.core.dto.RoleDto;
import com.iteaj.msn.core.entity.Role;
import org.apache.ibatis.annotations.Mapper;

import java.io.Serializable;
import java.util.List;

/**
 * create time: 2019/11/27
 *
 * @author iteaj
 * @since 1.0
 */
@Mapper
public interface IRoleDao extends BaseMapper<Role> {

    RoleDto joinRoleMenuById(Long id);

    void updateRolePermsById(Role role);

    void createRoleAndPerms(RoleDto role);

    void deleteAllJoinByIds(List<Long> list);

    List<Long> selectByAdminId(Serializable id);
}
