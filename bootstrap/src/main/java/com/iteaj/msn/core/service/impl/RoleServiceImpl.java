package com.iteaj.msn.core.service.impl;

import com.iteaj.msn.core.mapper.IRoleDao;
import com.iteaj.msn.core.dto.RoleDto;
import com.iteaj.msn.core.entity.Role;
import com.iteaj.msn.core.service.IRoleService;
import com.iteaj.util.CommonUtils;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.result.DetailResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * create time: 2019/11/27
 *
 * @author iteaj
 * @since 1.0
 */
@Service
public class RoleServiceImpl extends BaseServiceImpl<IRoleDao, Role> implements IRoleService {

    @Override
    @Transactional
    public void deleteAllJoinByIds(List<Long> list) {
        if(CommonUtils.isNotEmpty(list)) {
            getBaseMapper().deleteAllJoinByIds(list);
        }
    }

    @Override
    public DetailResult<RoleDto> detail(Long id) {
        return new DetailResult<>(getBaseMapper().joinRoleMenuById(id));
    }

    @Override
    public void createRoleAndPerms(RoleDto role) {
        getBaseMapper().createRoleAndPerms(role);
    }

    @Override
    public void updateRolePermsById(Role role) {
        getBaseMapper().updateRolePermsById(role);
    }

    @Override
    public List<Long> selectByAdminId(Serializable id) {
        return this.getBaseMapper().selectByAdminId(id);
    }
}
