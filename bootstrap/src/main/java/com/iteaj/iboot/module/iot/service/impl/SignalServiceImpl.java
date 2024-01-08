package com.iteaj.iboot.module.iot.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.iot.consts.IotConsts;
import com.iteaj.iboot.module.iot.entity.Signal;
import com.iteaj.iboot.module.iot.mapper.SignalMapper;
import com.iteaj.iboot.module.iot.service.IPointGroupService;
import com.iteaj.iboot.module.iot.service.ISignalService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 寄存器点位 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2022-07-22
 */
@Service
public class SignalServiceImpl extends BaseServiceImpl<SignalMapper, Signal> implements ISignalService {

    @Autowired
    private IPointGroupService pointGroupService;

    @Override
    public BooleanResult save(Signal entity) {
        DetailResult<Signal> detailResult = this.getOne(Wrappers.<Signal>lambdaQuery()
                .eq(Signal::getModelId, entity.getModelId())
                .eq(Signal::getAddress, entity.getAddress()));

        if(detailResult.getData() != null) {
            throw new ServiceException("点位地址已存在["+entity.getAddress()+"]");
        }

        return super.save(entity);
    }

    @Override
    public BooleanResult updateById(Signal entity) {
        DetailResult<Signal> detailResult = this.getOne(Wrappers.<Signal>lambdaQuery()
                .ne(Signal::getId, entity.getId())
                .eq(Signal::getModelId, entity.getModelId())
                .eq(Signal::getAddress, entity.getAddress()));

        if(detailResult.getData() != null) {
            throw new ServiceException("点位地址已存在["+entity.getAddress()+"]");
        }

        return super.updateById(entity);
    }

    @Override
    public Result<IPage<Signal>> detailByPage(Page<Signal> page, Signal entity) {
        return new PageResult<>(getBaseMapper().detailByPage(page, entity));
    }

    @Override
    @Transactional
    public void batchImport(List<Signal> signals) {
        if(!CollectionUtils.isEmpty(signals)) {
            for (int i = 0; i < signals.size(); i++) {
                Signal signal = signals.get(i);
                signal.setType(1); // 默认点位
                if(signal.getModelId() == null) {
                    throw new ServiceException("第["+(i + 2)+"]行设备型号未找到");
                }
                if(!StringUtils.hasText(signal.getName())) {
                    throw new ServiceException("第["+(i + 2)+"]行点位名称必填");
                }
                if(!StringUtils.hasText(signal.getFieldName())) {
                    throw new ServiceException("第["+(i + 2)+"]行字段名称必填");
                }
                if(!StringUtils.hasText(signal.getAddress())) {
                    throw new ServiceException("第["+(i + 2)+"]行点位地址必填");
                }
                if(signal.getFieldType() == null) {
                    throw new ServiceException("第["+(i + 2)+"]行字段类型必填");
                }
                if(signal.getFieldType() == IotConsts.FIELD_TYPE_FLOAT || signal.getFieldType() == IotConsts.FIELD_TYPE_DOUBLE) {
                    if(signal.getAccuracy() == null) {
                        throw new ServiceException("第["+(i + 2)+"]行未设置精度");
                    }
                }
            }

            for (int i = 0; i < signals.size(); i++) {
                Signal signal = signals.get(i);
                DetailResult<Signal> detailResult = this.getOne(Wrappers.<Signal>lambdaQuery()
                        .eq(Signal::getModelId, signal.getModelId())
                        .eq(Signal::getAddress, signal.getAddress()));

                if(detailResult.getData() != null) {
                    throw new ServiceException("第["+(i + 2)+"]行点位地址["+signal.getAddress()+"]已存在");
                }
            }

            this.saveBatch(signals);
        }
    }

    @Override
    public BooleanResult removeByIds(Collection<? extends Serializable> idList) {
        if(CollectionUtils.isEmpty(idList)) {
            return new BooleanResult(false);
        }

        if(pointGroupService.isBindSignals(idList)) {
            throw new ServiceException("此点位已被点位组使用");
        }

        return super.removeByIds(idList);
    }
}
