package com.iteaj.admin.service;

import com.iteaj.admin.dao.ITestDao;
import com.iteaj.admin.entity.Test;
import com.iteaj.util.module.mvc.IBaseService;

public interface ITestService extends IBaseService<ITestDao, Test> {

    String getTest();
}
