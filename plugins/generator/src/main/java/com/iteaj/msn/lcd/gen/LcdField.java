package com.iteaj.msn.lcd.gen;

import com.baomidou.mybatisplus.generator.config.po.TableField;
import lombok.Data;

@Data
public class LcdField extends TableField {

    /**
     * 是否编辑
     */
    private boolean edit;

    /**
     * 是否搜索
     */
    private boolean search;

    /**
     * 是否列表
     */
    private boolean table;

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
}
