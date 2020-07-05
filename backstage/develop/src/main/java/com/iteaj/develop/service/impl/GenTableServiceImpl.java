package com.iteaj.develop.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.develop.mapper.IGenTableDao;
import com.iteaj.develop.entity.GenField;
import com.iteaj.develop.entity.GenTable;
import com.iteaj.develop.dto.GenTableDto;
import com.iteaj.develop.enums.GenStatus;
import com.iteaj.develop.service.IGenFieldService;
import com.iteaj.develop.service.IGenTableService;
import com.iteaj.util.module.mvc.BaseServiceImpl;
import com.iteaj.util.module.mvc.result.BooleanResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * create time: 2020/4/27
 *
 * @author iteaj
 * @since 1.0
 */
@Service
public class GenTableServiceImpl extends BaseServiceImpl<IGenTableDao, GenTable> implements IGenTableService {

    @Autowired
    private IGenFieldService genFieldService;

    @Override
    @Transactional
    public BooleanResult insertAsBatch(Collection<GenTable> entityList) {
        if(CollectionUtils.isEmpty(entityList))
            return new BooleanResult(false).setMessage("不存在要导入的表");

        List<GenField> genFields = new ArrayList<>();
        entityList.forEach(item-> {
            GenTableDto tableDto = (GenTableDto) item;
            if(!CollectionUtils.isEmpty(tableDto.getFieldList())) {
                genFields.addAll(tableDto.getFieldList());
            }
        });

        super.insertAsBatch(entityList);
        this.genFieldService.insertAsBatch(genFields);
        return new BooleanResult(true);
    }

    @Override
    public List<GenTableDto> selectByJoinFields(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)) return Collections.EMPTY_LIST;

        return this.getBaseDao().selectByJoinFields(ids);
    }



    /**
     * 重新导入表
     * @param genTables
     */
    @Override
    @Transactional
    public void reImportTables(List<GenTable> genTables) {
        if(CollectionUtils.isEmpty(genTables))return;

        // 删除原来的数据
//        genTables.forEach(item->this.deleteByDbNameAndName(item.getDbName(), item.getName()));

        // 添加最新的数据
        this.insertAsBatch(genTables);
    }

    /**
     * 根据模块名称和表明删除
     * @param msn
     * @param name
     */
    @Override
    public void deleteByDbNameAndName(String msn, String name) {
        getBaseDao().deleteByDbNameAndName(msn, name);
    }

    @Override
    public void updateStatusByBatch(List<? extends GenTable>genTables, GenStatus status) {
        if(CollectionUtils.isEmpty(genTables)) return;

        Object[] array = genTables.stream().map(item -> item.getId()).toArray(Object[]::new);
        getBaseDao().update(new GenTable(status), Wrappers.<GenTable>update().in("id", array));
    }
}
