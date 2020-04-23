package com.iteaj.admin.service.impl;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.iteaj.admin.dao.IRoleDao;
import com.iteaj.admin.dto.RoleDto;
import com.iteaj.admin.entity.Role;
import com.iteaj.admin.service.IRoleService;
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
@SofaService(bindings = {@SofaServiceBinding(bindingType = "${bind.type}")})
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
