package com.iteaj.modules.dev.service;

import com.iteaj.modules.dev.dto.GenModuleDto;
import com.iteaj.modules.dev.entity.GenModule;
import com.iteaj.modules.dev.mapper.IGenModuleDao;
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
