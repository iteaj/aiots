package com.iteaj.msn.core.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.msn.core.entity.DictType;
import com.iteaj.msn.core.mapper.IDictTypeDao;
import com.iteaj.msn.core.service.IDictTypeService;
import org.springframework.stereotype.Service;

/**
 * jdk：1.8
 *
 * @author iteaj
 * create time：2019/7/13
 */
@Service
public class DictTypeServiceImpl extends BaseServiceImpl<IDictTypeDao, DictType> implements IDictTypeService {

    @Override
    public BooleanResult save(DictType entity) {
        getOne(Wrappers.<DictType>lambdaQuery().eq(DictType::getType, entity.getType())).ofNullable()
                .ifPresent(item -> {throw new ServiceException("字典类型重复["+entity.getType()+"]");});

        getOne(Wrappers.<DictType>lambdaQuery().eq(DictType::getName, entity.getName())).ofNullable()
                .ifPresent(item -> {throw new ServiceException("字典名称重复["+entity.getName()+"]");});

        return super.save(entity);
    }

    @Override
    public BooleanResult updateById(DictType entity) {
        getOne(Wrappers.<DictType>lambdaQuery().eq(DictType::getName, entity.getName())).ofNullable()
                .ifPresent(item -> {
                    if(item.getId().compareTo(entity.getId()) != 0) {
                        throw new ServiceException("字典名称重复["+entity.getName()+"]");
                    }
                });

        return super.updateById(entity);
    }
}
