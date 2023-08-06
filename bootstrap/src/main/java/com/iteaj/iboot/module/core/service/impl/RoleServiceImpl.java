package com.iteaj.iboot.module.core.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.ListResult;
import com.iteaj.iboot.module.core.dto.RoleDto;
import com.iteaj.iboot.module.core.entity.Role;
import com.iteaj.iboot.module.core.mapper.IRoleDao;
import com.iteaj.iboot.module.core.service.IRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collections;
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
    public BooleanResult updateById(Role entity) {
        this.getOne(Wrappers.<Role>lambdaQuery().eq(Role::getName, entity.getName()))
                .ofNullable().ifPresent(item -> {
                    if(item.getId().compareTo(entity.getId()) != 0) {
                        throw new ServiceException("角色名称重复["+entity.getName()+"]");
                    }
                });
        return super.updateById(entity);
    }

    /**
     * 删除角色和对应的菜单
     * @param list
     */
    @Override
    @Transactional
    public void delRoleAndPermByIds(List<Long> list) {
        if(CollectionUtil.isNotEmpty(list)) {
            if(list.size() > 1) {
                throw new ServiceException("不支持批量删除");
            }
            List<Long> adminIds = this.listBindAdminOfRole(list.get(0));
            if(CollectionUtil.isNotEmpty(adminIds)) {
                throw new ServiceException("此角色已被使用");
            }

            getBaseMapper().deleteAllJoinByIds(list);
        } else {
            throw new ServiceException("请选择要删除的角色");
        }
    }

    @Override
    public DetailResult<RoleDto> detail(Long id) {
        return new DetailResult<>(getBaseMapper().joinRoleMenuById(id));
    }

    @Override
    public void createRoleAndPerms(RoleDto role) {
        this.getOne(Wrappers.<Role>lambdaQuery().eq(Role::getName, role.getName())).ofNullable()
                .ifPresent(item -> {throw new ServiceException("角色名称重复["+role.getName()+"]");});

        getBaseMapper().createRoleAndPerms(role);
    }

    @Override
    public void updateRolePermsById(RoleDto role) {
        if(!CollectionUtil.isNotEmpty(role.getMenuIds())) {
            throw new ServiceException("请选择权限");
        }
        this.getOne(Wrappers.<Role>lambdaQuery().eq(Role::getName, role.getName()))
                .ofNullable().ifPresent(item -> {
                    // 设备名称已存在
                    if(role.getId().compareTo(item.getId()) != 0) {
                        throw new ServiceException("角色名称重复["+role.getName()+"]");
                    }
                });

        getBaseMapper().updateRolePermsById(role);
    }

    @Override
    public List<Long> selectByAdminId(Serializable id) {
        return this.getBaseMapper().selectByAdminId(id);
    }

    @Override
    public ListResult<Long> listMenusOfRole(Long roleId) {
        if(roleId == null) {
            return new ListResult<>(Collections.EMPTY_LIST);
        }

        return new ListResult<>(getBaseMapper().listMenusOfRole(roleId));
    }

    @Override
    public List<Long> listBindAdminOfRole(Long roleId) {
        return getBaseMapper().listBindAdminOfRole(roleId);
    }
}
