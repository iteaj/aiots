package com.iteaj.admin.service.impl;

import com.iteaj.admin.dao.ITestDao;
import com.iteaj.admin.entity.Test;
import com.iteaj.admin.service.ITestService;
import com.iteaj.util.module.mvc.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
class TestServiceImpl extends BaseServiceImpl<ITestDao, Test> implements ITestService {

    public TestServiceImpl() {

    }

    @Override
    public String getTest() {
        return "Test";
    }
}
