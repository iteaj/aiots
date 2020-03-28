package com.iteaj.admin.service.impl;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.admin.dao.IModuleDao;
import com.iteaj.admin.entity.Module;
import com.iteaj.admin.service.IModuleService;
import com.iteaj.util.module.mvc.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
@SofaService(bindings = {@SofaServiceBinding(bindingType = "${bind.type}")})
public class ModuleServiceImpl extends
        BaseServiceImpl<IModuleDao, Module> implements IModuleService {

    @Override
    public Module getByMsn(String msn) {
        return getBaseDao().selectOne(Wrappers.<Module>query().eq("msn", msn));
    }
}
