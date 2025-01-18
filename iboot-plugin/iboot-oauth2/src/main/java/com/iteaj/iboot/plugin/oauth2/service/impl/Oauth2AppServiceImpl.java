package com.iteaj.iboot.plugin.oauth2.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.iboot.plugin.oauth2.entity.Oauth2App;
import com.iteaj.iboot.plugin.oauth2.mapper.Oauth2AppMapper;
import com.iteaj.iboot.plugin.oauth2.service.Oauth2AppService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = {"IBOOT_CACHE_OATH2_APP"})
public class Oauth2AppServiceImpl extends BaseServiceImpl<Oauth2AppMapper, Oauth2App> implements Oauth2AppService {

    @Override
    @Cacheable(key = "#clientId")
    public DetailResult<Oauth2App> getByClientId(String clientId) {
        return this.getOne(Wrappers.<Oauth2App>lambdaQuery()
                .eq(Oauth2App::getClientId, clientId));
    }

    @Override
    @CacheEvict(key = "#clientId")
    public BooleanResult removeByClientId(String clientId) {
        return this.remove(Wrappers.<Oauth2App>lambdaQuery().eq(Oauth2App::getClientId, clientId));
    }

    @Override
    public BooleanResult save(Oauth2App entity) {
        this.getOne(Wrappers.<Oauth2App>lambdaQuery()
                .eq(Oauth2App::getClientName, entity.getClientName()))
                .ifPresentThrow("应用已经存在["+entity.getClientName()+"]");
        String salt = RandomUtil.randomString(5);
        String appId = SecureUtil.md5(salt);
        String appSecure = SecureUtil.sha1(appId + salt);
        entity.setClientId(appId);
        entity.setClientSecret(appSecure);
        return super.save(entity);
    }

    @Override
    @CacheEvict(key = "#entity.clientId")
    public BooleanResult updateById(Oauth2App entity) {
        return super.updateById(entity);
    }

}
