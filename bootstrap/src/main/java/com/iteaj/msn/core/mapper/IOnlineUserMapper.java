package com.iteaj.msn.core.mapper;

import com.iteaj.msn.core.entity.OnlineUser;
import com.iteaj.util.module.mvc.orm.IBaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * create time: 2020/6/20
 *
 * @author iteaj
 * @since 1.0
 */
@Mapper
public interface IOnlineUserMapper extends IBaseDao<OnlineUser> {

}
