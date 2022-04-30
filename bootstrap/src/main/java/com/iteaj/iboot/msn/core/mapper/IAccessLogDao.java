package com.iteaj.iboot.msn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iteaj.iboot.msn.core.entity.AccessLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * create time: 2020/4/22
 *
 * @author iteaj
 * @since 1.0
 */
@Mapper
public interface IAccessLogDao extends BaseMapper<AccessLog> {

}
