package com.iteaj.iboot.module.iot.service;

import com.iteaj.iboot.module.iot.entity.Gateway;
import com.iteaj.framework.IBaseService;

/**
 * <p>
 * 协议网关 服务类
 * </p>
 *
 * @author iteaj
 * @since 2023-09-12
 */
public interface IGatewayService extends IBaseService<Gateway> {

    boolean isBindProduct(Long gatewayId);
}
