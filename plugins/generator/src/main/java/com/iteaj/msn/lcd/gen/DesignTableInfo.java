package com.iteaj.msn.lcd.gen;

import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class DesignTableInfo extends TableInfo {

    private String dtoName;

    private final Set<String> dtoImportPackages = new HashSet<>();

    /**
     * 主键字段
     */
    private String keyField;

    /**
     * 表关联信息
     */
    private List<JoinTable> joinTables;

    /**
     * dto字段信息
     */
    private List<LcdField> dtoFields;

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

    public String getJoinColumns() {
        if(CollectionUtils.isEmpty(dtoFields)) {
            return "";
        }
        return ", " + dtoFields.stream().map(item -> item.getAlias() + "." + item.getColumnName() +" " +
                item.getPropertyName()).collect(Collectors.joining(", "));
    }
}
