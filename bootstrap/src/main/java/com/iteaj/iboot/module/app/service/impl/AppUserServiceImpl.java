package com.iteaj.iboot.module.app.service.impl;

import com.iteaj.iboot.module.app.entity.AppUser;
import com.iteaj.iboot.module.app.mapper.AppUserMapper;
import com.iteaj.iboot.module.app.service.IAppUserService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * App用户管理 服务实现类
 * @author iteaj
 */
@Service
public class AppUserServiceImpl extends BaseServiceImpl<AppUserMapper, AppUser> implements IAppUserService {

}
