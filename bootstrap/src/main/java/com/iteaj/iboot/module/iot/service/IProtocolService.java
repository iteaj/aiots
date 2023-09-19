package com.iteaj.iboot.module.iot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.iot.dto.ProtocolDto;
import com.iteaj.iboot.module.iot.entity.Protocol;
import com.iteaj.framework.IBaseService;

/**
 * <p>
 * 报文协议 服务类
 * </p>
 *
 * @author iteaj
 * @since 2023-09-12
 */
public interface IProtocolService extends IBaseService<Protocol> {

    PageResult<IPage<ProtocolDto>> pageDetail(Page<Protocol> page, ProtocolDto entity);
}
