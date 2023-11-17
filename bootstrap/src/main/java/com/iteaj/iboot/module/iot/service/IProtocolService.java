package com.iteaj.iboot.module.iot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.*;
import com.iteaj.iboot.module.iot.dto.ProductDto;
import com.iteaj.iboot.module.iot.dto.ProtocolDto;
import com.iteaj.iboot.module.iot.dto.ProtocolToProductModel;
import com.iteaj.iboot.module.iot.entity.Protocol;
import com.iteaj.framework.IBaseService;

import java.util.List;

/**
 * <p>
 * 报文协议 服务类
 * </p>
 *
 * @author iteaj
 * @since 2023-09-12
 */
public interface IProtocolService extends IBaseService<Protocol> {

    DetailResult<ProtocolDto> getByDetail(Long id);

    PageResult<IPage<ProtocolDto>> pageDetail(Page<Protocol> page, ProtocolDto entity);
}
