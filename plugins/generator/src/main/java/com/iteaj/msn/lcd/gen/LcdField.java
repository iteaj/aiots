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
}
