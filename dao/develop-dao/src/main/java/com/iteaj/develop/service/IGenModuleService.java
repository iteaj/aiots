package com.iteaj.develop.service;

import com.iteaj.develop.mapper.IGenModuleDao;
import com.iteaj.develop.entity.GenModule;
import com.iteaj.develop.entity.dto.GenModuleDto;
import com.iteaj.util.module.mvc.IBaseService;

import java.io.Serializable;

/**
 * create time: 2020/4/27
 *
 * @author iteaj
 * @since 1.0
 */
public interface IGenModuleService extends IBaseService<IGenModuleDao, GenModule> {

    GenModuleDto joinGenTableById(Serializable id);

    void createGenTables(Long id);

    GenModule getByMsn(String msn);

}
