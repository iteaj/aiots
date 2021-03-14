package com.iteaj.dev.mapper;

import com.iteaj.dev.dto.GenTableDto;
import com.iteaj.dev.entity.GenTable;
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
