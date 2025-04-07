package com.iteaj.iboot.plugin.script.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.result.ListResult;
import com.iteaj.iboot.plugin.script.entity.Script;
import com.iteaj.iboot.plugin.script.mapper.ScriptMapper;
import com.iteaj.iboot.plugin.script.service.IScriptService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 脚本引擎 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2025-04-04
 */
@Service
public class ScriptServiceImpl extends BaseServiceImpl<ScriptMapper, Script> implements IScriptService {

    @Override
    public ListResult<Script> listByType(String type) {
        return this.list(Wrappers.<Script>lambdaQuery().eq(Script::getType, type));
    }
}
