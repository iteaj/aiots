package com.iteaj.admin.service.impl;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.iteaj.admin.dao.IConfigDao;
import com.iteaj.admin.entity.Config;
import com.iteaj.admin.service.IConfigService;
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
@SofaService(bindings = {@SofaServiceBinding(bindingType = "${bind.type}")})
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
