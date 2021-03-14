package com.iteaj.modules.dev.mapper;

import com.iteaj.modules.dev.dto.GenModuleDto;
import com.iteaj.modules.dev.entity.GenModule;
import com.iteaj.util.module.mvc.orm.IBaseDao;

import java.io.Serializable;

/**
 * create time: 2020/4/27
 *
 * @author iteaj
 * @since 1.0
 */
public interface IGenModuleDao extends IBaseDao<GenModule> {

    GenModuleDto joinGenTableById(Serializable id);
}
