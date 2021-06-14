package com.iteaj.msn.core.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.msn.core.entity.OnlineUser;
import com.iteaj.msn.core.mapper.IOnlineUserMapper;
import com.iteaj.msn.core.service.IOnlineUserService;
import com.iteaj.framework.spi.OnlineSession;
import com.iteaj.framework.spi.admin.auth.OnlineService;
import com.iteaj.framework.spi.auth.LoginEvent;
import com.iteaj.framework.spi.event.FrameworkListener;
import com.iteaj.util.module.mvc.BaseServiceImpl;
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
        implements IOnlineUserService, OnlineService, FrameworkListener<LoginEvent> {

    @Override
    @Transactional
    public void updateBySessionId(OnlineUser onlineUser) {
        getBaseDao().update(onlineUser, Wrappers.<OnlineUser>update()
                .eq("session_id", onlineUser.getSessionId()));
    }

    @Override
    public OnlineSession getSession(Serializable sessionId) {
        return getBaseDao().selectOne(Wrappers.<OnlineUser>query()
                .eq("session_id", sessionId));
    }

    @Override
    public void removeBySessionId(Serializable sessionId) {
        getBaseDao().delete(Wrappers.<OnlineUser>query().eq("session_id", sessionId));
    }

    @Override
    public void update(OnlineSession session) {
        updateById((OnlineUser) session);
    }

    @Override
    public void onFrameworkEvent(LoginEvent event) {
        System.out.println(event);
    }
}
