package com.iteaj.iboot.plugin.oauth2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iteaj.framework.IVOption;
import com.iteaj.iboot.plugin.oauth2.entity.Oauth2App;

import java.util.List;

public interface Oauth2AppMapper extends BaseMapper<Oauth2App> {

    List<IVOption> listAdminAccount();
}
