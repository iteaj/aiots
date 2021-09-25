package com.iteaj.msn.core.service;

import com.iteaj.framework.IBaseService;
import com.iteaj.framework.result.ListResult;
import com.iteaj.msn.core.entity.Organize;

/**
 * create time: 2019/11/26
 *
 * @author iteaj
 * @since 1.0
 */
public interface IOrganizeService extends IBaseService<Organize> {

    ListResult<Organize> selectTrees(Organize organize);
}
