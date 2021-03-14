package com.iteaj.modules.core.mapper;

import com.iteaj.modules.core.entity.OperaLog;
import com.iteaj.util.module.mvc.orm.IBaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * create time: 2020/4/22
 *
 * @author iteaj
 * @since 1.0
 */
@Mapper
public interface IOperaLogDao extends IBaseDao<OperaLog> {

}
