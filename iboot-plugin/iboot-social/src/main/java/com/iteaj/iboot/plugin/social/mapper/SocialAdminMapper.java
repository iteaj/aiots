package com.iteaj.iboot.plugin.social.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iteaj.iboot.plugin.social.entity.SocialAdmin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理端用户 Mapper（微信登录自动注册用）
 * @author iteaj
 */
@Mapper
public interface SocialAdminMapper extends BaseMapper<SocialAdmin> {

}
