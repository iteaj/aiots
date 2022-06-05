package com.iteaj.iboot.msn.iot.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.utils.TreeUtils;
import com.iteaj.iboot.msn.iot.entity.DeviceType;
import com.iteaj.iboot.msn.iot.mapper.DeviceTypeMapper;
import com.iteaj.iboot.msn.iot.service.IDeviceTypeService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 设备类型 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2022-05-15
 */
@Service
public class DeviceTypeServiceImpl extends BaseServiceImpl<DeviceTypeMapper, DeviceType> implements IDeviceTypeService {

    @Override
    public ListResult<DeviceType> tree(DeviceType entity) {
        List<DeviceType> types = this.list(Wrappers.lambdaQuery(entity)).ofNullable()
                .map(value -> TreeUtils.toTrees(value)
                        .stream().collect(Collectors.toList())).get();
        return new ListResult<>(types);
    }
}
