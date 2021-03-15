package com.iteaj.dev.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.dev.dto.GenModuleDto;
import com.iteaj.dev.entity.GenModule;
import com.iteaj.dev.mapper.IGenModuleDao;
import com.iteaj.dev.service.IGenModuleService;
import com.iteaj.dev.service.IGenTableService;
import com.iteaj.util.module.mvc.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * create time: 2020/4/27
 *
 * @author iteaj
 * @since 1.0
 */
@Service
public class GenModuleServiceImpl extends BaseServiceImpl<IGenModuleDao, GenModule> implements IGenModuleService {

    @Autowired
    private IGenTableService genTableService;

    @Override
    public GenModuleDto joinGenTableById(Serializable id) {
        return getBaseDao().joinGenTableById(id);
    }

    @Override
    public void createGenTables(Long id) {

    }

    @Override
    public GenModule getByMsn(String msn) {
        return getBaseDao().selectOne(Wrappers.<GenModule>query().eq("msn", msn));
    }
}
