package com.iteaj.core.mapper;

import com.iteaj.core.entity.Config;
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
