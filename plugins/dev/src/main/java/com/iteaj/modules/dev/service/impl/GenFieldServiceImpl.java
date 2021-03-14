package com.iteaj.modules.dev.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.modules.dev.entity.GenField;
import com.iteaj.modules.dev.mapper.IGenFieldDao;
import com.iteaj.modules.dev.service.IGenFieldService;
import com.iteaj.util.module.mvc.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * create time: 2020/4/28
 *
 * @author iteaj
 * @since 1.0
 */
@Service
public class GenFieldServiceImpl extends BaseServiceImpl<IGenFieldDao, GenField> implements IGenFieldService {

    @Override
    public List<GenField> selectByDbNameAndTableName(String dbName, String name) {
        return getBaseDao().selectList(Wrappers.<GenField>query()
                .eq("db_name", dbName).eq("table_name", name));
    }
}
