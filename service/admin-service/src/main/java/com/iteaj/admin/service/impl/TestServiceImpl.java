package com.iteaj.admin.service.impl;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.iteaj.admin.dao.ITestDao;
import com.iteaj.admin.entity.Test;
import com.iteaj.admin.service.ITestService;
import com.iteaj.util.module.mvc.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
@SofaService(bindings = {@SofaServiceBinding(bindingType = "${bind.type}")})
class TestServiceImpl extends BaseServiceImpl<ITestDao, Test> implements ITestService {

    public TestServiceImpl() {

    }

    @Override
    public String getTest() {
        return "Test";
    }
}
