package com.iteaj.msn.lcd.gen;

import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import lombok.Data;

import java.util.List;

@Data
public class LcdTableInfo extends TableInfo {

    /**
     * 编辑表单字段
     */
    private List<FormField> formFields;

    /**
     * 搜索表单字段
     */
    private List<FormField> searchFields;

    /**
     * 列表字段
     */
    private List<FormTableField> formTableFields;

}
