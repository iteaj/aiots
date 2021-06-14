package com.iteaj.msn.core.mapper;

import com.iteaj.msn.core.entity.Config;
import com.iteaj.util.module.mvc.orm.IBaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * create time: 2019/12/4
 *
 * @author iteaj
 * @since 1.0
 */
@Mapper
public interface IConfigDao extends IBaseDao<Config> {

}
