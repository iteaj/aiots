package com.iteaj.iboot.module.iot.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.spi.iot.DeviceProtocolSupplier;
import com.iteaj.framework.spi.iot.ProtocolSupplierManager;
import com.iteaj.framework.spi.iot.consts.ProtocolImplMode;
import com.iteaj.framework.spi.iot.protocol.AbstractEventProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelAttr;
import com.iteaj.iboot.module.iot.dto.ProtocolDto;
import com.iteaj.iboot.module.iot.entity.*;
import com.iteaj.iboot.module.iot.mapper.ProtocolMapper;
import com.iteaj.iboot.module.iot.service.IProtocolService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 报文协议 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2023-09-12
 */
@Service
public class ProtocolServiceImpl extends BaseServiceImpl<ProtocolMapper, Protocol> implements IProtocolService {

    @Override
    public BooleanResult save(Protocol entity) {
        ProtocolDto protocolDto = loadProtocol(entity.getCode());
        if(protocolDto == null) {
            throw new ServiceException("没有找到对应的协议["+entity.getCode()+"]");
        }

        this.getByCode(entity.getCode()).ifPresentThrow("协议代码已存在["+entity.getCode()+"]");

        entity.setVersion(protocolDto.getVersion());
        return super.save(entity);
    }

    @Override
    public BooleanResult updateById(Protocol entity) {
        ProtocolDto protocolDto = loadProtocol(entity.getCode());
        if(protocolDto == null) {
            throw new ServiceException("没有找到对应的协议["+entity.getCode()+"]");
        }

        this.getByCode(entity.getCode()).ifPresent(item -> {
            if(!item.getId().equals(entity.getId())) {
                throw new ServiceException("协议代码已存在["+entity.getCode()+"]");
            }
        });

        entity.setVersion(protocolDto.getVersion());
        return super.updateById(entity);
    }

    @Override
    public BooleanResult removeByIds(Collection<? extends Serializable> idList) {
        if(!this.isBindGateway(idList)) {
            return super.removeByIds(idList);
        } else {
            return new BooleanResult(false, "此协议已被网关绑定");
        }
    }

    @Override
    public PageResult<IPage<ProtocolDto>> pageDetail(Page<Protocol> page, ProtocolDto entity) {
        return new PageResult<>(getBaseMapper().pageDetail(page, entity));
    }

    @Override
    public ProtocolDto loadProtocol(String code) {
        DeviceProtocolSupplier supplier = ProtocolSupplierManager.get(code);
        if(supplier != null) {
            ProtocolDto protocolDto = new ProtocolDto();
            protocolDto.setVersion(supplier.getVersion());
            ProtocolModel protocol = supplier.getProtocol();
            // 协议接口
            protocol.getApis().values().stream().forEach(api -> {
                // 协议接口
                ProtocolApi protocolApi = new ProtocolApi()
                        .setProtocolCode(protocol.getCode())
                        .setCode(api.getCode())
                        .setName(api.getName())
                        .setType(api.getType())
                        .setRemark(api.getRemark());

                // 事件协议
                if(api instanceof AbstractEventProtocolModelApi) {
                    protocolApi.setTriggerMode(((AbstractEventProtocolModelApi) api).getTriggerMode());
                }

                // 协议接口上行配置
                List<ProtocolApiConfig> upConfig = api.getUpConfig()
                        .values().stream().map(config -> {
                            ProtocolModelAttr attr = protocol.getAttrs().get(config.getProtocolModelAttrField());
                            // 将协议模型的api配置转换成协议api配置
                            return convertProtocolApiConfig(protocolApi, config, attr);
                        }).collect(Collectors.toList());

                // 协议接口下行配置
                List<ProtocolApiConfig> downConfig = api.getDownConfig()
                        .values().stream().map(config -> {
                            ProtocolModelAttr attr = protocol.getAttrs().get(config.getProtocolModelAttrField());
                            // 将协议模型的api配置转换成协议api配置
                            return convertProtocolApiConfig(protocolApi, config, attr);
                        }).collect(Collectors.toList());

                protocolDto.addApis(protocolApi.setUpConfigs(upConfig).setDownConfigs(downConfig));
            });

            // 协议属性
            List<ProtocolAttr> attrs = protocol.getAttrs().values().stream()
                    .map(attr -> new ProtocolAttr()
                            .setProtocolCode(protocol.getCode())
                            .setName(attr.getName())
                            .setField(attr.getField())
                            .setRemark(attr.getRemark())
                            .setType(attr.getType()))
                    .collect(Collectors.toList());
            protocolDto.setAttrs(attrs);

            return protocolDto;
        }

        return null;
    }

    @Override
    public Boolean isBindGateway(Collection<? extends Serializable> idList) {
        return getBaseMapper().isBindGateway(idList);
    }

    @Override
    public DetailResult<Protocol> getByCode(String code) {
        return getOne(Wrappers.<Protocol>lambdaQuery().eq(Protocol::getCode, code));
    }

    private ProtocolApiConfig convertProtocolApiConfig(ProtocolApi protocolApi, ProtocolModelApiConfig config, ProtocolModelAttr attr) {
        return new ProtocolApiConfig()
                .setSort(config.getSort())
                .setLength(config.getLength())
                .setRemark(config.getRemark())
                .setPosition(config.getPosition())
                .setProtocolApiCode(protocolApi.getCode())
                .setAttrName(attr != null ? attr.getName() : null)
                .setAttrType(attr != null ? attr.getType() : null)
                .setAttrRemark(attr != null ? attr.getRemark() : null)
                .setProtocolAttrField(config.getProtocolModelAttrField());
    }

    @Override
    public DetailResult<ProtocolDto> getByDetail(Long id) {
        ProtocolDto detail = this.getBaseMapper().getByDetail(id);
        if(detail != null && detail.getImplMode() != ProtocolImplMode.Custom) {
            ProtocolDto protocolDto = this.loadProtocol(detail.getCode());
            if(protocolDto != null) {
                detail.setAttrs(protocolDto.getAttrs());
                detail.setFuncApis(protocolDto.getFuncApis());
                detail.setEventApis(protocolDto.getEventApis());
            }
        }

        return new DetailResult<>(detail);
    }
}
