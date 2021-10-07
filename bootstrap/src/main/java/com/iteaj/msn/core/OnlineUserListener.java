package com.iteaj.msn.core;

import com.iteaj.framework.spi.admin.event.OnlineEvent;
import com.iteaj.framework.spi.admin.event.OnlineStatus;
import com.iteaj.framework.spi.event.FrameworkListener;
import com.iteaj.msn.core.entity.Admin;
import com.iteaj.msn.core.entity.OnlineUser;
import com.iteaj.msn.core.enums.ClientType;
import com.iteaj.msn.core.service.IOnlineUserService;
import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 监听用户在线状态
 */
@Component
public class OnlineUserListener implements FrameworkListener<OnlineEvent> {

    private final IOnlineUserService service;

    public OnlineUserListener(IOnlineUserService service) {
        this.service = service;
    }

    @Async
    @Override
    public void onFrameworkEvent(OnlineEvent event) {
        OnlineStatus type = event.getType();
        OnlineUser onlineUser = new OnlineUser((String) event.getSource());

        if(type == OnlineStatus.Online) { // 用户上线
            Admin admin = (Admin) event.getAdmin(); // 当前登录的用户
            final UserAgent agent = event.getUserAgent();
            final String browse = agent.getBrowser().getName();
            final String os = agent.getOperatingSystem().getName();
            final DeviceType deviceType = agent.getOperatingSystem().getDeviceType();
            final ClientType clientType = ClientType.valueOf(deviceType.name());
            onlineUser.setLoginTime(new Date())
                    .setOs(os)
                    .setStatus(type)
                    .setBrowse(browse)
                    .setType(clientType)
                    .setUserNick(admin.getName())
                    .setAccount(admin.getAccount())
                    .setAccessIp(event.getAccessIp())
                    .setExpireTime(event.getExpireTime());

            service.save(onlineUser);
        } else if(type == OnlineStatus.Offline) { // 用户下线
            service.updateBySessionId(onlineUser.setStatus(type).setUpdateTime(new Date()));
        }
    }
}
