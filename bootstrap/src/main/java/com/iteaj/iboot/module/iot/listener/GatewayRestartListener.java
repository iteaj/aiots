package com.iteaj.iboot.module.iot.listener;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.spi.event.ApplicationReadyListener;
import com.iteaj.iboot.module.iot.consts.GatewayStatus;
import com.iteaj.iboot.module.iot.entity.Gateway;
import com.iteaj.iboot.module.iot.service.IGatewayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;

/**
 * 网关重启监听
 */
public class GatewayRestartListener implements ApplicationReadyListener, Ordered {

    private final IGatewayService gatewayService;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public GatewayRestartListener(IGatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    @Override
    public void started(ApplicationContext context) {
        // 启用所有已经存在的网关
        this.gatewayService.list(Wrappers.<Gateway>lambdaQuery().eq(Gateway::getStatus, GatewayStatus.start)).forEach(item -> {
            try {
                this.gatewayService.switchStatus(item.getId(), GatewayStatus.start);
            } catch (Exception e) {
                logger.error("<<< 启动网关[{}]失败 {}", item.getName(), e);
                this.gatewayService.update(Wrappers.<Gateway>lambdaUpdate()
                        .eq(Gateway::getId, item.getId())
                        .set(Gateway::getReason, e.getMessage()));
            }
        });
    }

    /**
     * 网关必须比产品提前执行
     * @return
     */
    @Override
    public int getOrder() {
        return 17168;
    }
}
