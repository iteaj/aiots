package com.iteaj.admin.service;

import com.iteaj.admin.dao.IModuleDao;
import com.iteaj.admin.entity.Module;
import com.iteaj.util.module.mvc.IBaseService;

public interface IModuleService extends IBaseService<IModuleDao, Module> {

    /**
     * 通过模块编号获取模块
     * @param msn
     * @return
     */
    Module getByMsn(String msn);
}
