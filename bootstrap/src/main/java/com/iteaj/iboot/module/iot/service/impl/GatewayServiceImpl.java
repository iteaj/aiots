package com.iteaj.iboot.module.iot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.iboot.module.iot.consts.GatewayStatus;
import com.iteaj.iboot.module.iot.dto.GatewayDto;
import com.iteaj.iboot.module.iot.entity.Gateway;
import com.iteaj.iboot.module.iot.mapper.GatewayMapper;
import com.iteaj.iboot.module.iot.service.IGatewayService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;

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

    @Override
    public BooleanResult removeByIds(Collection<? extends Serializable> idList) {
        if(CollectionUtil.isEmpty(idList)) {
            return BooleanResult.buildFalse("未指定要删除的网关");
        }

        this.list(Wrappers.<Gateway>lambdaQuery()
            .in(Gateway::getId, idList))
            .forEach(gateway -> {
                if(gateway.getStatus() == GatewayStatus.start) {
                    throw new ServiceException("请先停止网关["+gateway.getName()+"]后再删除");
                }

                if(this.isBindProduct(gateway.getId())) {
                    throw new ServiceException("此网关["+gateway.getName()+"]已被绑定");
                }
            });

        return super.removeByIds(idList);
    }

    @Override
    public boolean isBindProduct(Long gatewayId) {
        return this.getBaseMapper().isBindProduct(gatewayId);
    }

    @Override
    public DetailResult<GatewayDto> detailById(Long id) {
        return new DetailResult<>(this.getBaseMapper().detailById(id));
    }
}
