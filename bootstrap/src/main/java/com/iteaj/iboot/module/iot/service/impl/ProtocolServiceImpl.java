package com.iteaj.iboot.module.iot.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.spi.iot.DeviceProtocolSupplier;
import com.iteaj.framework.spi.iot.ProtocolSupplierManager;
import com.iteaj.framework.spi.iot.consts.ProtocolImplMode;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelAttr;
import com.iteaj.iboot.module.iot.dto.ProtocolDto;
import com.iteaj.iboot.module.iot.dto.ProtocolToProductModel;
import com.iteaj.iboot.module.iot.entity.*;
import com.iteaj.iboot.module.iot.mapper.ProtocolMapper;
import com.iteaj.iboot.module.iot.service.IProtocolService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

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
    public PageResult<IPage<ProtocolDto>> pageDetail(Page<Protocol> page, ProtocolDto entity) {
        return new PageResult<>(getBaseMapper().pageDetail(page, entity));
    }

    @Override
    public DetailResult<ProtocolDto> getByDetail(Long id) {
        ProtocolDto detail = this.getBaseMapper().getByDetail(id);
        return new DetailResult<>(detail);
    }
}
