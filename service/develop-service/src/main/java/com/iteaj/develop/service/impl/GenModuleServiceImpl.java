package com.iteaj.develop.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Objects;
import com.iteaj.develop.dao.IGenModuleDao;
import com.iteaj.develop.entity.GenModule;
import com.iteaj.develop.entity.GenTable;
import com.iteaj.develop.entity.dto.GenModuleDto;
import com.iteaj.develop.service.IGenModuleService;
import com.iteaj.develop.service.IGenTableService;
import com.iteaj.util.CommonUtils;
import com.iteaj.util.module.mvc.BaseServiceImpl;
import com.iteaj.util.module.mvc.ServiceException;
import com.iteaj.util.module.mvc.result.ListResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
