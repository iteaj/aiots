package com.iteaj.modules.dev.service;


import com.iteaj.modules.dev.entity.GenField;
import com.iteaj.modules.dev.mapper.IGenFieldDao;
import com.iteaj.util.module.mvc.IBaseService;

import java.util.List;

/**
 * create time: 2020/4/28
 *
 * @author iteaj
 * @since 1.0
 */
public interface IGenFieldService extends IBaseService<IGenFieldDao, GenField> {

    List<GenField> selectByDbNameAndTableName(String dbName, String name);
}
