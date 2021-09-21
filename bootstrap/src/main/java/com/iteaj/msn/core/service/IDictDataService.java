package com.iteaj.msn.core.service;

import com.iteaj.msn.core.mapper.IDictDataDao;
import com.iteaj.msn.core.entity.DictData;
import com.iteaj.framework.IBaseService;
import com.iteaj.framework.result.ListResult;

/**
 * jdk：1.8
 *
 * @author iteaj
 * create time：2019/6/24
 */
public interface IDictDataService extends IBaseService<IDictDataDao, DictData> {

    ListResult<DictData> selectByType(String type);
}
