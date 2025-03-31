package com.iteaj.iboot.plugin.open.service.impl;

import com.iteaj.iboot.plugin.open.entity.Northward;
import com.iteaj.iboot.plugin.open.mapper.NorthwardMapper;
import com.iteaj.iboot.plugin.open.service.INorthwardService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 北向接入 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2025-02-07
 */
@Service
public class NorthwardServiceImpl extends BaseServiceImpl<NorthwardMapper, Northward> implements INorthwardService {

    @Override
    public List<Northward> listByType(String type) {
        return null;
    }
}
