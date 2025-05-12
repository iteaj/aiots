package com.iteaj.iboot.plugin.script.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.iot.listener.filter.ListenerScriptFilter;
import com.iteaj.iboot.plugin.script.entity.Script;
import com.iteaj.iboot.plugin.script.service.IScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 监听过滤器脚本
 * @author iteaj
 * @since 2025-04-04
 */
@RestController
@RequestMapping("/iot/script/filter")
public class ListenerScriptController extends BaseController {

    private final IScriptService scriptService;
    private final List<ListenerScriptFilter> filters;

    public ListenerScriptController(IScriptService scriptService
            , @Autowired(required = false) List<ListenerScriptFilter> filters) {
        this.scriptService = scriptService;
        this.filters = filters;
    }

    /**
     * 获取过滤功能的脚本列表
     * @return
     */
    @GetMapping("filters")
    public Result<List<Script>> filters() {
        List<Script> result = new ArrayList<>();
        final List<Script> scripts = scriptService.listByType(Script.TYPE_FILTER).of().orElse(Collections.EMPTY_LIST);
        if(filters != null) {
            final Map<String, Script> collect = scripts.stream()
                    .collect(Collectors.toMap(script -> script.getName(), scriptDto -> scriptDto));
            filters.forEach(filter -> {
                final Script scriptDto = collect.get(filter.getFilterName());
                if(scriptDto != null) {
                    result.add(scriptDto.setCompletes(filter.getCompletes()));
                } else {
                    result.add(new Script().setName(filter.getFilterName())
                            .setCompletes(filter.getCompletes())
                            .setRemark(filter.getScriptRemark()).setType(Script.TYPE_FILTER)
                            .setScript((JSONArray) JSON.toJSON(filter.getScripts())));
                }
            });
        }
        return success(result);
    }
}
