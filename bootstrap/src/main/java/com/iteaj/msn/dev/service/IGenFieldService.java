package com.iteaj.msn.dev.service;


import com.iteaj.msn.dev.entity.GenField;
import com.iteaj.msn.dev.mapper.IGenFieldDao;
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
