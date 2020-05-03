package com.iteaj.develop.dao;

import com.iteaj.develop.entity.GenTable;
import com.iteaj.develop.entity.dto.GenTableDto;
import com.iteaj.util.module.mvc.orm.IBaseDao;
import org.apache.ibatis.annotations.Param;

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
