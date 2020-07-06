package com.iteaj.develop.service;

import com.iteaj.develop.mapper.IGenTableDao;
import com.iteaj.develop.entity.GenTable;
import com.iteaj.develop.dto.GenTableDto;
import com.iteaj.develop.enums.GenStatus;
import com.iteaj.util.module.mvc.IBaseService;

import java.util.List;

/**
 * create time: 2020/4/27
 *
 * @author iteaj
 * @since 1.0
 */
public interface IGenTableService extends IBaseService<IGenTableDao, GenTable> {

    List<GenTableDto> selectByJoinFields(List<Long> ids);

    void reImportTables(List<GenTable> genTables);

    void updateStatusByBatch(List<? extends GenTable> genTables, GenStatus status);

}
