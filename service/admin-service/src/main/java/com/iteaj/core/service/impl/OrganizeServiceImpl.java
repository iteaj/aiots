package com.iteaj.core.service.impl;

import com.iteaj.core.mapper.IOrganizeDao;
import com.iteaj.core.entity.Organize;
import com.iteaj.core.service.IOrganizeService;
import com.iteaj.framework.utils.TreeUtils;
import com.iteaj.util.module.mvc.BaseServiceImpl;
import com.iteaj.util.module.mvc.result.ListResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * create time: 2019/11/26
 *
 * @author iteaj
 * @since 1.0
 */
@Service
public class OrganizeServiceImpl extends BaseServiceImpl<IOrganizeDao, Organize> implements IOrganizeService {

    @Override
    public ListResult<Organize> selectTrees(Organize organize) {
        ListResult<Organize> selectList = this.selectList(organize);
        return new ListResult((List<Organize>) TreeUtils.toTrees(selectList.get(), 0l));
    }
}
