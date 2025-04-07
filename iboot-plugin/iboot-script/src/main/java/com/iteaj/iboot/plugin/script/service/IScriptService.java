package com.iteaj.iboot.plugin.script.service;

import com.iteaj.framework.result.ListResult;
import com.iteaj.iboot.plugin.script.entity.Script;
import com.iteaj.framework.IBaseService;

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
}
