package com.iteaj.modules.core.service.impl;

import com.iteaj.modules.core.mapper.IConfigDao;
import com.iteaj.modules.core.entity.Config;
import com.iteaj.modules.core.service.IConfigService;
import com.iteaj.util.module.mvc.BaseServiceImpl;
import com.iteaj.util.module.mvc.ServiceException;
import com.iteaj.util.module.mvc.result.BooleanResult;
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
    public BooleanResult insert(Config entity) {
        try {
            return super.insert(entity);
        } catch (Exception e) {
            if(e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new ServiceException("配置标签重复("+entity.getLabel()+")");
            } else {
                throw new ServiceException(e.getMessage(), e);
            }
        }
    }
}
