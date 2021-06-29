package com.iteaj.msn.core.mapper;

import com.iteaj.msn.core.entity.AccessLog;
import com.iteaj.util.module.mvc.orm.IBaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * create time: 2020/4/22
 *
 * @author iteaj
 * @since 1.0
 */
@Mapper
public interface IAccessLogDao extends IBaseDao<AccessLog> {

}
