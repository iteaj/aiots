package com.iteaj.iboot.msn.core.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.spi.OnlineSession;
import com.iteaj.framework.spi.admin.auth.OnlineService;
import com.iteaj.framework.spi.admin.event.OnlineStatus;
import com.iteaj.iboot.msn.core.entity.OnlineUser;
import com.iteaj.iboot.msn.core.mapper.IOnlineUserMapper;
import com.iteaj.iboot.msn.core.service.IOnlineUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * <p>
 * 在线用户 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2020-06-20
 */
@Service
public class OnlineUserServiceImpl extends BaseServiceImpl<IOnlineUserMapper, OnlineUser>
        implements IOnlineUserService, OnlineService {

    @Override
    @Transactional
    public void updateBySessionId(OnlineUser onlineUser) {
        getBaseMapper().update(onlineUser, Wrappers.<OnlineUser>update()
                .eq("session_id", onlineUser.getSessionId()));
    }

    @Override
    public OnlineSession getSession(Serializable sessionId) {
        return getBaseMapper().selectOne(Wrappers.<OnlineUser>lambdaQuery()
                .eq(OnlineUser::getSessionId, sessionId)
                .eq(OnlineUser::getStatus, OnlineStatus.Online));
    }

    @Override
    public void removeBySessionId(Serializable sessionId) {
        getBaseMapper().delete(Wrappers.<OnlineUser>query().eq("session_id", sessionId));
    }

    @Override
    public void update(OnlineSession session) {
        updateById((OnlineUser) session);
    }

}
