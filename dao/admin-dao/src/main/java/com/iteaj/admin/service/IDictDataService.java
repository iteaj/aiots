package com.iteaj.admin.service;

import com.iteaj.admin.dao.IDictDataDao;
import com.iteaj.admin.entity.DictData;
import com.iteaj.util.module.mvc.IBaseService;
import com.iteaj.util.module.mvc.result.ListResult;

/**
 * jdk：1.8
 *
 * @author iteaj
 * create time：2019/6/24
 */
public interface IDictDataService extends IBaseService<IDictDataDao, DictData> {

    ListResult<DictData> selectByType(String type);
}
