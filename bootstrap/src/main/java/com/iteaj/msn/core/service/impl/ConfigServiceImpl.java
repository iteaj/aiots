package com.iteaj.msn.core.service.impl;

import com.iteaj.msn.core.mapper.IConfigDao;
import com.iteaj.msn.core.entity.Config;
import com.iteaj.msn.core.service.IConfigService;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * create time: 2019/12/4
 *
 * @author iteaj
 * @since 1.0
 */
@Service
public class ConfigServiceImpl extends BaseServiceImpl<IConfigDao, Config> implements IConfigService {

    @Override
    public BooleanResult save(Config entity) {
        try {
            return super.save(entity);
        } catch (Exception e) {
            if(e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new ServiceException("配置标签重复("+entity.getLabel()+")");
            } else {
                throw new ServiceException(e.getMessage(), e);
            }
        }
    }
}
