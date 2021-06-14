package com.iteaj.msn.core.service.impl;

import com.iteaj.msn.core.mapper.IRoleDao;
import com.iteaj.msn.core.dto.RoleDto;
import com.iteaj.msn.core.entity.Role;
import com.iteaj.msn.core.service.IRoleService;
import com.iteaj.util.CommonUtils;
import com.iteaj.util.module.mvc.BaseServiceImpl;
import com.iteaj.util.module.mvc.result.DetailResult;
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
            getBaseDao().deleteAllJoinByIds(list);
        }
    }

    @Override
    public DetailResult<RoleDto> detail(Long id) {
        return new DetailResult<>(getBaseDao().joinRoleMenuById(id));
    }

    @Override
    public void createRoleAndPerms(RoleDto role) {
        getBaseDao().createRoleAndPerms(role);
    }

    @Override
    public void updateRoleAndPermsById(Role role) {
        getBaseDao().updateRoleAndPermsById(role);
    }

    @Override
    public List<Long> selectByAdminId(Serializable id) {
        return this.getBaseDao().selectByAdminId(id);
    }
}
