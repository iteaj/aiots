package com.iteaj.msn.core.service;

import com.iteaj.framework.IBaseService;
import com.iteaj.framework.result.ListResult;
import com.iteaj.msn.core.entity.DictData;

/**
 * jdk：1.8
 *
 * @author iteaj
 * create time：2019/6/24
 */
public interface IDictDataService extends IBaseService<DictData> {

    ListResult<DictData> selectByType(String type);
}
