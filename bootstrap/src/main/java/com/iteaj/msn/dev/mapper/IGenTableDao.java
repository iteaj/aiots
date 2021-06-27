package com.iteaj.msn.dev.mapper;

import com.iteaj.msn.dev.dto.GenTableDto;
import com.iteaj.msn.dev.entity.GenTable;
import com.iteaj.util.module.mvc.orm.IBaseDao;

import java.util.List;

/**
 * create time: 2020/4/27
 *
 * @author iteaj
 * @since 1.0
 */
public interface IGenTableDao extends IBaseDao<GenTable> {

    List<GenTableDto> selectByJoinFields(List<Long> ids);

    void deleteByDbNameAndName(String dbName, String name);
}
