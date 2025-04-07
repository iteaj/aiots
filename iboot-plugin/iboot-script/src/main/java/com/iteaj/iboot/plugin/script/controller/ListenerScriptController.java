package com.iteaj.iboot.plugin.script.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.framework.spi.iot.listener.filter.ListenerScriptFilter;
import com.iteaj.iboot.plugin.script.dto.FilterScriptDto;
import com.iteaj.iboot.plugin.script.entity.Script;
import com.iteaj.iboot.plugin.script.service.IScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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
    public Result<List<FilterScriptDto>> filters() {
        List<FilterScriptDto> result = new ArrayList<>();
        scriptService.listByType("filter").ifPresent(scripts -> {
            if(filters != null) {
//                final Map<String, FilterScriptDto> collect = scripts.stream().map(item -> (FilterScriptDto)new FilterScriptDto()
//                        .setScript(item.getScript()).setLanguage(item.getLanguage())
//                        .setName(item.getName()).setRemark(item.getRemark()).setType(item.getType()))
//                        .collect(Collectors.toMap(script -> script.getName(), scriptDto -> scriptDto));
//                filters.forEach(filter -> {
//                    final FilterScriptDto scriptDto = collect.get(filter.getFilterName());
//                    if(scriptDto != null) {
//                        result.add(scriptDto.setConfigs(filter.getScripts()));
//                    } else {
//                        result.add(new FilterScriptDto().setName(filter.getFilterName()).set)
//                    }
//                });
            }
        });

        return success(result);
    }
}
