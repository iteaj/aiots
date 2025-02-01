package com.iteaj.iboot.plugin.oauth2.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.Entity;
import com.iteaj.framework.IVOption;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.iboot.plugin.oauth2.entity.Oauth2App;
import com.iteaj.iboot.plugin.oauth2.mapper.Oauth2AppMapper;
import com.iteaj.iboot.plugin.oauth2.service.Oauth2AppService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.iteaj.iboot.plugin.oauth2.service.impl.Oauth2AppServiceImpl.cacheName;

@Service
@CacheConfig(cacheNames = {cacheName})
public class Oauth2AppServiceImpl extends BaseServiceImpl<Oauth2AppMapper, Oauth2App> implements Oauth2AppService {

    private final Cache cache;
    public static final String cacheName = "IBOOT_CACHE_OATH2_APP";

    public Oauth2AppServiceImpl(CacheManager cacheManager) {
        this.cache = cacheManager.getCache(cacheName);
    }

    @Override
    public DetailResult<Oauth2App> getByClientId(String clientId) {
        final Cache.ValueWrapper valueWrapper = cache.get(clientId);
        if(valueWrapper != null) {
            return new DetailResult((Entity) valueWrapper.get());
        }

        final DetailResult<Oauth2App> one = this.getOne(Wrappers.<Oauth2App>lambdaQuery()
                .eq(Oauth2App::getClientId, clientId));

        return one.ifPresent(entity -> cache.put(clientId, entity));
    }

    @Override
    public BooleanResult removeByClientId(String clientId) {
        cache.evict(clientId);
        return this.remove(Wrappers.<Oauth2App>lambdaQuery().eq(Oauth2App::getClientId, clientId));
    }

    @Override
    public List<IVOption> listAdminAccount() {
        return this.getBaseMapper().listAdminAccount();
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
        return super.save(entity)
                .ifPresentAndTrue(status -> cache.put(entity.getClientId(), entity));
    }

    @Override
    public BooleanResult updateById(Oauth2App entity) {
        return super.updateById(entity)
                .ifPresentAndTrue(status -> cache.put(entity.getClientId(), entity));
    }

}
