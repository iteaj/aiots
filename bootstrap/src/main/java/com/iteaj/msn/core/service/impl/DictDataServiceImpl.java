package com.iteaj.msn.core.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.msn.core.mapper.IDictDataDao;
import com.iteaj.msn.core.entity.DictData;
import com.iteaj.msn.core.service.IDictDataService;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.result.ListResult;
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
        List<DictData> list = getBaseMapper().selectList(Wrappers.<DictData>query().eq("type", type));
        return new ListResult<>(list);
    }
}
