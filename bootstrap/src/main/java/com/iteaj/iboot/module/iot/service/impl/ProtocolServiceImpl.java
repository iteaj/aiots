package com.iteaj.iboot.module.iot.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.iboot.module.iot.dto.ProtocolDto;
import com.iteaj.iboot.module.iot.entity.Protocol;
import com.iteaj.iboot.module.iot.mapper.ProtocolMapper;
import com.iteaj.iboot.module.iot.service.IProtocolService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

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
