package com.iteaj.iboot.module.iot.mapper;

import com.iteaj.iboot.module.iot.entity.Gateway;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 协议网关 Mapper 接口
 * </p>
 *
 * @author iteaj
 * @since 2023-09-12
 */
public interface GatewayMapper extends BaseMapper<Gateway> {

    Boolean isBindProduct(Long gatewayId);
}
