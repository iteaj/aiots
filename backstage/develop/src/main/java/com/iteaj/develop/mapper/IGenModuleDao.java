package com.iteaj.develop.mapper;

import com.iteaj.develop.entity.GenModule;
import com.iteaj.develop.dto.GenModuleDto;
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
