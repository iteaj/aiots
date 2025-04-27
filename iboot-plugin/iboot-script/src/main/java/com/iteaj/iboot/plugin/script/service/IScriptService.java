package com.iteaj.iboot.plugin.script.service;

import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.plugin.script.entity.BindDto;
import com.iteaj.iboot.plugin.script.entity.Script;
import com.iteaj.framework.IBaseService;

import java.util.List;

/**
 * <p>
 * 脚本引擎 服务类
 * </p>
 *
 * @author iteaj
 * @since 2025-04-04
 */
public interface IScriptService extends IBaseService<Script> {

    ListResult<Script> listByType(String type);

    /**
     * 是否被关联
     * @param name
     * @return
     */
    Boolean isJoinModelApi(String name);

    Boolean isJoinModelAttr(String key);

    ListResult<BindDto> bindApis(Long id);

    ListResult<BindDto> bindAttrs(Long id);
}
