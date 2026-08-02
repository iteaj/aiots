package com.iteaj.iboot.plugin.oauth2.service;

import com.iteaj.framework.IBaseService;
import com.iteaj.framework.IVOption;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.plugin.oauth2.entity.Oauth2App;

import java.util.List;

public interface Oauth2AppService extends IBaseService<Oauth2App> {

    DetailResult<Oauth2App> getByClientId(String clientId);

    BooleanResult removeByClientId(String clientId);

    List<IVOption> listAdminAccount();
}
