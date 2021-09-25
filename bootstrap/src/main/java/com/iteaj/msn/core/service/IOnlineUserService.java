package com.iteaj.msn.core.service;

import com.iteaj.msn.core.mapper.IOnlineUserMapper;
import com.iteaj.framework.IBaseService;
import com.iteaj.msn.core.entity.OnlineUser;

/**
 * <p>
 * 在线用户 服务类
 * </p>
 *
 * @author iteaj
 * @since 2020-06-20
 */
public interface IOnlineUserService extends IBaseService<OnlineUser> {

    void updateBySessionId(OnlineUser onlineUser);
}
