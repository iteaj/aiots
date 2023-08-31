package com.iteaj.iboot.plugin.message.service;

import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.message.MessageConfig;
import com.iteaj.framework.spi.message.SendModel;
import com.iteaj.framework.spi.message.email.EmailMessageService;
import org.dromara.email.api.MailClient;
import org.dromara.email.comm.config.MailSmtpConfig;
import org.dromara.email.core.factory.MailFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Sms4jEmailMessageService extends EmailMessageService {

    private Map<String, MailClient> cacheClient = new HashMap<>();

    @Override
    public void send(MessageConfig config, SendModel model) {
        String smtpServer = config.getConfig("smtpServer");
        String fromAddress = config.getConfig("fromAddress");
        if(StrUtil.isBlank(smtpServer) || StrUtil.isBlank(fromAddress)) {
            throw new ServiceException("[smtpServer或fromAddress]不能为空");
        }

        if(StrUtil.isBlank(model.getAccept())) {
            throw new ServiceException("收件人邮箱[accept]不能为空");
        }

        String clientKey = smtpServer + ":" + fromAddress;
        MailClient mailClient = cacheClient.get(clientKey);
        if(mailClient == null) {
            synchronized (this) {
                mailClient = cacheClient.get(clientKey);
                if(mailClient == null) {
                    MailSmtpConfig mailSmtpConfig = MailSmtpConfig.builder()
                            .port(config.getConfig("port"))
                            .username(config.getConfig("username"))
                            .password(config.getConfig("password"))
                            .smtpServer(smtpServer)
                            .fromAddress(fromAddress)
                            .build();

                    MailFactory.put(clientKey, mailSmtpConfig);
                    mailClient = MailFactory.createMailClient(clientKey);
                    cacheClient.put(clientKey, mailClient);
                }
            }
        }

        // knlijuorwpetbhij
        String[] send = model.getAccept().split(",");
        mailClient.sendMail(Arrays.asList(send), model.getTitle(), model.getContent().toString());
    }
}
