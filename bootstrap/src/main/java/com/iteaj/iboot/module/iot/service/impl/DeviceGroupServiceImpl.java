package com.iteaj.iboot.module.iot.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.utils.TreeUtils;
import com.iteaj.iboot.module.iot.consts.GroupType;
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
        if(entity.getType() == null) {
            return BooleanResult.buildFalse("组类型必填");
        }
        DeviceGroup parent = null;
        DetailResult<DeviceGroup> detailResult = this.getById(entity.getPid());
        super.save(entity.setCreateTime(new Date()));
        if(entity.getPid() != null && entity.getPid() != 0) {
            parent = detailResult
                    .ifNotPresentThrow("父设备组不存在")
                    .getData();
            entity.setPath(parent.getPath()+","+entity.getId());
        } else {
            entity.setPid(0l).setPath(entity.getId().toString());
        }

        validateParent(entity, parent);
        return this.update(Wrappers.<DeviceGroup>lambdaUpdate()
                .set(DeviceGroup::getPath, entity.getPath())
                .set(DeviceGroup::getGroupProductId, entity.getGroupProductId())
                .eq(DeviceGroup::getId, entity.getId()));
    }

    private void validateParent(DeviceGroup entity, DeviceGroup parent) {
        if(parent == null) {
            if(entity.getType() != GroupType.archive) {
                throw new ServiceException("顶级分组的组类型只能是归档类型");
            }
        } else {
            if(parent.getType() == GroupType.product) {
                if(entity.getType() != GroupType.device) {
                    throw new ServiceException("产品组下面必须是设备组");
                }
            } else if(parent.getType() == GroupType.device) {
                throw new ServiceException("设备组下面不允许新增组");
            } else if(parent.getType() == GroupType.archive) {
                if(entity.getType() == GroupType.device) {
                    throw new ServiceException("归档组下面只允许挂载归档组或产品组");
                }
            }
        }
    }

    @Override
    public BooleanResult updateById(DeviceGroup entity) {
        if(entity.getType() == null) {
            return BooleanResult.buildFalse("组类型必填");
        }

        this.getById(entity.getId()).ifNotPresentThrow("分组不存在")
                .ifPresent(item -> {
            if(item.getPid() == 0 && !entity.getPid().equals(item.getPid())) { // 不允许修改顶级的父级
                throw new ServiceException("不允许修改顶级到其他组");
            }
        });

        this.getOne(Wrappers.<DeviceGroup>lambdaQuery()
                .eq(DeviceGroup::getName, entity.getName())
                .ne(DeviceGroup::getId, entity.getId()))
                .ifPresentThrow("分组名称已存在["+entity.getName()+"]");

        DeviceGroup parent = null;
        if(entity.getPid() != null && entity.getPid() != 0) {
            parent = this.getById(entity.getPid())
                    .ifNotPresentThrow("父分组不存在")
                    .getData();
            entity.setPath(parent.getPath()+","+entity.getId());
        } else {
            entity.setPid(0l).setPath(entity.getId().toString());
        }

        validateParent(entity, parent);
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
    public DetailResult<DeviceGroup> getGroupProductById(Long id) {
        return new DetailResult<>(getBaseMapper().getGroupProductById(id));
    }

    @Override
    public boolean hasBindEventSource(Collection<? extends Serializable> idList) {
        return this.getBaseMapper().hasBindEventSource(idList);
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

        // 校验是否已被事件源绑定
        if(this.hasBindEventSource(idList)) {
            throw new ServiceException("请先解绑此分组下的事件源");
        }

        return super.removeByIds(idList);
    }
}
