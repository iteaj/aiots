package com.iteaj.msn.lcd.gen;

import com.baomidou.mybatisplus.generator.config.po.TableField;
import lombok.Data;

@Data
public class LcdField extends TableField {


    /**
     * 字段所属表
     * 用于引用字段
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
}
