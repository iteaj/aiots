package com.iteaj.iboot.module.iot.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.utils.TreeUtils;
import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.iteaj.iboot.module.iot.entity.ProductType;
import com.iteaj.iboot.module.iot.mapper.DeviceGroupMapper;
import com.iteaj.iboot.module.iot.service.IDeviceGroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 设备分组 服务实现类
 * </p>
 *
 * @author iteaj
 */
@Service
public class DeviceGroupServiceImpl extends BaseServiceImpl<DeviceGroupMapper, DeviceGroup> implements IDeviceGroupService {

    @Override
    @Transactional
    public BooleanResult save(DeviceGroup entity) {
        this.getOne(Wrappers.<DeviceGroup>lambdaQuery().eq(DeviceGroup::getName, entity.getName()))
                .ifPresentThrow("分组名称已存在["+entity.getName()+"]");

        super.save(entity.setCreateTime(new Date()));
        if(entity.getPid() != null && entity.getPid() != 0) {
            DeviceGroup parent = this.getById(entity.getPid())
                    .ifNotPresentThrow("父类型不存在")
                    .getData();
            entity.setPath(parent.getPath()+","+entity.getId());
        } else {
            entity.setPath(entity.getId().toString());
        }

        return this.update(Wrappers.<DeviceGroup>lambdaUpdate()
                .set(DeviceGroup::getPath, entity.getPath())
                .eq(DeviceGroup::getId, entity.getId()));
    }

    @Override
    public BooleanResult updateById(DeviceGroup entity) {
        this.getOne(Wrappers.<DeviceGroup>lambdaQuery()
                .eq(DeviceGroup::getName, entity.getName())
                .ne(DeviceGroup::getId, entity.getId()))
                .ifPresentThrow("分组名称已存在["+entity.getName()+"]");

        if(entity.getPid() != null && entity.getPid() != 0) {
            DeviceGroup parent = this.getById(entity.getPid())
                    .ifNotPresentThrow("父分组不存在")
                    .getData();

            entity.setPath(parent.getPath()+","+entity.getId());
        } else {
            entity.setPath(entity.getId().toString());
        }

        return super.updateById(entity);
    }

    @Override
    public ListResult<DeviceGroup> tree(DeviceGroup entity) {
        return list().ifPresent((list, result) -> {
            List<DeviceGroup> deviceGroups = TreeUtils.toTrees(list)
                    .stream().collect(Collectors.toList());
            result.setData(deviceGroups);
        });
    }

    @Override
    public boolean hasBindDevice(Collection<?> idList) {
        return getBaseMapper().hasBindDevice(idList);
    }

    @Override
    public DetailResult<DeviceGroup> detailById(Long id) {
        return new DetailResult<>(getBaseMapper().detailById(id));
    }

    @Override
    public BooleanResult removeByIds(Collection<? extends Serializable> idList) {
        this.count(Wrappers.<DeviceGroup>lambdaQuery().in(DeviceGroup::getPid, idList))
                .ifPresent(count -> {
            if(count > 0) {
                throw new ServiceException("请先删除子分组");
            }
        });

        // 校验是否已绑定设备
        if(this.hasBindDevice(idList)) {
            throw new ServiceException("请先解绑此分组下的设备");
        }

        return super.removeByIds(idList);
    }
}
