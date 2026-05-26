package com.iteaj.iboot.module.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iteaj.iboot.module.app.entity.AppUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * App用户 Mapper 接口
 * @author iteaj
 */
@Mapper
public interface AppUserMapper extends BaseMapper<AppUser> {

}
