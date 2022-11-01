package com.iteaj.iboot.msn.iot.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
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

    @Override
    public BooleanResult save(DeviceType entity) {
        super.save(entity);
        if(entity.getPid() != null && entity.getPid() != 0) {
            DeviceType parent = this.getById(entity.getPid())
                    .ofNullable().orElseThrow(() -> new ServiceException("父类型不存在"));
            entity.setPath(parent.getPath()+","+entity.getId());
        } else {
            entity.setPath(entity.getId().toString());
        }

        return this.update(Wrappers.<DeviceType>lambdaUpdate()
                .set(DeviceType::getPath, entity.getPath())
                .eq(DeviceType::getId, entity.getId()));
    }

    @Override
    public BooleanResult updateById(DeviceType entity) {
        if(entity.getPid() != null && entity.getPid() != 0) {
            DeviceType parent = this.getById(entity.getPid())
                    .ofNullable().orElseThrow(() -> new ServiceException("父类型不存在"));

            entity.setPath(parent.getPath()+","+entity.getId());
        } else {
            entity.setPath(entity.getId().toString());
        }
        return super.updateById(entity);
    }
}
