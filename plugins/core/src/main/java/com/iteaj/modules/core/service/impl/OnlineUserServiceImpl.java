package com.iteaj.modules.core.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.modules.core.entity.OnlineUser;
import com.iteaj.modules.core.mapper.IOnlineUserMapper;
import com.iteaj.modules.core.service.IOnlineUserService;
import com.iteaj.framework.spi.OnlineSession;
import com.iteaj.framework.spi.admin.auth.OnlineService;
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
        implements IOnlineUserService, OnlineService {

    @Override
    @Transactional
    public void updateBySessionId(OnlineUser onlineUser) {
        getBaseDao().update(onlineUser, Wrappers.<OnlineUser>update()
                .eq("session_id", onlineUser.getSessionId()));
    }

    @Override
    public OnlineSession getOnlineSession(Serializable sessionId) {
        return getBaseDao().selectOne(Wrappers.<OnlineUser>query()
                .eq("session_id", sessionId));
    }
}
