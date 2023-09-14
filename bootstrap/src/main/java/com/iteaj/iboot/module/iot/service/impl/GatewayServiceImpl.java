package com.iteaj.iboot.module.iot.service.impl;

import com.iteaj.framework.result.BooleanResult;
import com.iteaj.iboot.module.iot.entity.Gateway;
import com.iteaj.iboot.module.iot.mapper.GatewayMapper;
import com.iteaj.iboot.module.iot.service.IGatewayService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 协议网关 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2023-09-12
 */
@Service
public class GatewayServiceImpl extends BaseServiceImpl<GatewayMapper, Gateway> implements IGatewayService {

    @Override
    public BooleanResult save(Gateway entity) {
        return super.save(entity);
    }

    @Override
    public BooleanResult updateById(Gateway entity) {
        return super.updateById(entity);
    }
}
