package com.iteaj.msn.core.service;

import com.iteaj.msn.core.mapper.IOrganizeDao;
import com.iteaj.msn.core.entity.Organize;
import com.iteaj.util.module.mvc.IBaseService;
import com.iteaj.util.module.mvc.result.ListResult;

/**
 * create time: 2019/11/26
 *
 * @author iteaj
 * @since 1.0
 */
public interface IOrganizeService extends IBaseService<IOrganizeDao, Organize> {

    ListResult<Organize> selectTrees(Organize organize);
}
