package com.iteaj.iboot.plugin.code.vue;

import com.baomidou.mybatisplus.generator.ITemplate;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class VueTemplate implements ITemplate {

    @Override
    public @NotNull Map<String, Object> renderData(@NotNull TableInfo tableInfo) {
        return Map.of();
    }
}
