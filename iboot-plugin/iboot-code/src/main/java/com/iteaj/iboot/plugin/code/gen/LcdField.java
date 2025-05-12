package com.iteaj.iboot.plugin.code.gen;

import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class LcdField extends TableField {

    private String alias;

    /**
     * 表单类型
     */
    private String formType;

    /**
     * 字段所属表
     */
    private String table;

    /**
     * 字段标题
     */
    private String title;

    /**
     * excel注解
     */
    private String excel;

    /**
     * 校验注解
     */
    private String validate;

    public LcdField(@NotNull ConfigBuilder configBuilder, @NotNull String name, String alias) {
        super(configBuilder, name);
        this.alias = alias;
    }
}
