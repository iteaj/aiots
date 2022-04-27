package com.iteaj.msn.core.config.listener;

import com.iteaj.framework.spi.admin.event.OnlinePayload;
import com.iteaj.framework.spi.admin.event.OnlineStatus;
import com.iteaj.framework.spi.event.FrameworkListener;
import com.iteaj.framework.spi.event.PayloadEvent;
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
public class OnlineUserListener implements FrameworkListener<PayloadEvent<OnlinePayload>> {

    private final IOnlineUserService service;

    public OnlineUserListener(IOnlineUserService service) {
        this.service = service;
    }

    @Async
    @Override
    public void onApplicationEvent(PayloadEvent<OnlinePayload> event) {
        final OnlinePayload payload = event.getPayload();
        OnlineStatus type = payload.getType();
        OnlineUser onlineUser = new OnlineUser(payload.getSessionId());

        if(type == OnlineStatus.Online) { // 用户上线
            final Admin admin = (Admin) payload.getUser();
            final UserAgent agent = payload.getUserAgent();
            final String browse = agent.getBrowser().getName();
            final String os = agent.getOperatingSystem().getName();
            final DeviceType deviceType = agent.getOperatingSystem().getDeviceType();
            final ClientType clientType = ClientType.valueOf(deviceType.name());
            onlineUser.setLoginTime(new Date())
                    .setOs(os).setStatus(type).setBrowse(browse)
                    .setType(clientType).setUserNick(admin.getName())
                    .setAccount(admin.getAccount()).setAccessIp(payload.getAccessIp())
                    .setExpireTime(payload.getExpireTime());

            service.save(onlineUser);
        } else if(type == OnlineStatus.Offline) { // 用户下线
            service.updateBySessionId(onlineUser.setStatus(type).setUpdateTime(new Date()));
        }
    }

}
