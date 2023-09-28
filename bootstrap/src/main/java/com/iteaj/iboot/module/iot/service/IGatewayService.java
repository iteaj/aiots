package com.iteaj.iboot.module.iot.service;

import com.iteaj.framework.IBaseService;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.iboot.module.iot.dto.GatewayDto;
import com.iteaj.iboot.module.iot.entity.Gateway;

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

    DetailResult<GatewayDto> detailById(Long id);
}
