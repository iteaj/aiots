package com.iteaj.iboot.plugin.script.mapper;

import com.iteaj.iboot.plugin.script.entity.BindDto;
import com.iteaj.iboot.plugin.script.entity.Script;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 脚本引擎 Mapper 接口
 * </p>
 *
 * @author iteaj
 * @since 2025-04-04
 */
public interface ScriptMapper extends BaseMapper<Script> {

    Boolean isJoinModelApi(String name);

    Boolean isJoinModelAttr(String key);

    List<BindDto> bindApis(String key);

    List<BindDto> bindAttrs(String key);
}
