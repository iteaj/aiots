package com.iteaj.modules.dev.service;

import com.iteaj.modules.dev.dto.GenTableDto;
import com.iteaj.modules.dev.entity.GenTable;
import com.iteaj.modules.dev.enums.GenStatus;
import com.iteaj.modules.dev.mapper.IGenTableDao;
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
