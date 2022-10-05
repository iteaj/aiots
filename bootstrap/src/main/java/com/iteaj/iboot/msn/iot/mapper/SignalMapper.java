package com.iteaj.iboot.msn.iot.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 寄存器点位 Mapper 接口
 * </p>
 *
 * @author iteaj
 * @since 2022-07-22
 */
public interface SignalMapper extends BaseMapper<Signal> {

    IPage<Signal> detailByPage(Page<Signal> page, Signal entity);
}
