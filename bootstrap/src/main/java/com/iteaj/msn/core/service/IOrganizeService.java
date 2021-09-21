package com.iteaj.msn.core.service;

import com.iteaj.msn.core.mapper.IOrganizeDao;
import com.iteaj.msn.core.entity.Organize;
import com.iteaj.framework.IBaseService;
import com.iteaj.framework.result.ListResult;

/**
 * create time: 2019/11/26
 *
 * @author iteaj
 * @since 1.0
 */
public interface IOrganizeService extends IBaseService<IOrganizeDao, Organize> {

    ListResult<Organize> selectTrees(Organize organize);
}
