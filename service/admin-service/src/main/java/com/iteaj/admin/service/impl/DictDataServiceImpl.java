package com.iteaj.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.admin.dao.IDictDataDao;
import com.iteaj.admin.entity.DictData;
import com.iteaj.admin.service.IDictDataService;
import com.iteaj.util.module.mvc.BaseServiceImpl;
import com.iteaj.util.module.mvc.result.ListResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * jdk：1.8
 *
 * @author iteaj
 * create time：2019/6/24
 */
@Service
public class DictDataServiceImpl extends BaseServiceImpl<IDictDataDao, DictData> implements IDictDataService {

    @Override
    public ListResult<DictData> selectByType(String type) {
        List<DictData> list = getBaseDao().selectList(Wrappers.<DictData>query().eq("type", type));
        return new ListResult<>(list);
    }
}
