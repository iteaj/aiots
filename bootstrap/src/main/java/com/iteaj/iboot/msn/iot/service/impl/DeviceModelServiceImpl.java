package com.iteaj.iboot.msn.iot.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.iboot.msn.iot.entity.DeviceModel;
import com.iteaj.iboot.msn.iot.entity.PointGroup;
import com.iteaj.iboot.msn.iot.entity.PointPosition;
import com.iteaj.iboot.msn.iot.mapper.DeviceModelMapper;
import com.iteaj.iboot.msn.iot.service.IDeviceModelService;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.iboot.msn.iot.service.IPointGroupService;
import com.iteaj.iboot.msn.iot.service.IPointPositionService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;

/**
 * <p>
 * 设备型号 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2022-07-22
 */
@Service
public class DeviceModelServiceImpl extends BaseServiceImpl<DeviceModelMapper, DeviceModel> implements IDeviceModelService {

    private final IPointGroupService pointGroupService;
    private final IPointPositionService pointPositionService;

    public DeviceModelServiceImpl(IPointGroupService pointGroupService, IPointPositionService pointPositionService) {
        this.pointGroupService = pointGroupService;
        this.pointPositionService = pointPositionService;
    }

    @Override
    public BooleanResult save(DeviceModel entity) {
        this.getOne(Wrappers.<DeviceModel>lambdaQuery()
                .eq(DeviceModel::getTypeId, entity.getTypeId())
                .eq(DeviceModel::getModel, entity.getModel()))
                .ofNullable().ifPresent(one -> {throw new ServiceException("此设备型号已经存在["+entity.getModel()+"]");});

        return super.save(entity);
    }

    @Override
    public BooleanResult updateById(DeviceModel entity) {
        this.getOne(Wrappers.<DeviceModel>lambdaQuery()
                .ne(DeviceModel::getId, entity.getId())
                .eq(DeviceModel::getTypeId, entity.getTypeId())
                .eq(DeviceModel::getModel, entity.getModel()))
                .ofNullable().ifPresent(one -> {throw new ServiceException("此设备型号已经存在["+entity.getModel()+"]");});

        return super.updateById(entity);
    }

    @Override
    public BooleanResult removeByIds(Collection<? extends Serializable> idList) {
        if(CollectionUtils.isEmpty(idList)) {
            return new BooleanResult(false);
        }
        if(idList.size() > 1) {
            return new BooleanResult(false).fail("不支持批量删除");
        }

        Serializable modelId = idList.stream().findFirst().get();
        this.pointGroupService.getOne(Wrappers.<PointGroup>lambdaQuery()
                .eq(PointGroup::getModelId, modelId)
                .last("limit 1")).ofNullable()
                .ifPresent(pointGroup -> {throw new ServiceException("点位组已在使用, 请先删除");});

        this.pointPositionService.getOne(Wrappers.<PointPosition>lambdaQuery()
                .eq(PointPosition::getModelId, modelId)
                .last("limit 1")).ofNullable()
                .ifPresent(pointPosition -> {throw new ServiceException("点位记录已在使用, 请先删除");});

        return super.removeByIds(idList);
    }
}
