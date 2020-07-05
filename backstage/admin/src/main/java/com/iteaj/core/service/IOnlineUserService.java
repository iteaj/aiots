package com.iteaj.core.service;

import com.iteaj.util.module.mvc.IBaseService;
import com.iteaj.core.entity.OnlineUser;
import com.iteaj.core.mapper.IOnlineUserMapper;

/**
 * <p>
 * 在线用户 服务类
 * </p>
 *
 * @author iteaj
 * @since 2020-06-20
 */
public interface IOnlineUserService extends IBaseService<IOnlineUserMapper,OnlineUser> {

    void updateBySessionId(OnlineUser onlineUser);
}
