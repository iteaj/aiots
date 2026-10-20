package com.iteaj.iboot.plugin.oauth2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iteaj.iboot.plugin.oauth2.entity.Oauth2Admin;
import com.iteaj.iboot.plugin.oauth2.entity.Oauth2ClientUser;

public interface Oauth2ClientMapper extends BaseMapper<Oauth2ClientUser> {

    void insertSysAdmin(Oauth2Admin oauth2Admin);
}
