package com.iteaj.iboot.plugin.satoken.impl;

import cn.dev33.satoken.stp.StpInterface;
import com.iteaj.framework.security.AuthorizationService;

import java.io.Serializable;
import java.util.List;

public class StpInterfaceImpl implements StpInterface {

    private final AuthorizationService authorizationService;

    public StpInterfaceImpl(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return (List<String>) authorizationService.getPermissions((Serializable) loginId);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return (List<String>) authorizationService.getRoles((Serializable) loginId);
    }
}
