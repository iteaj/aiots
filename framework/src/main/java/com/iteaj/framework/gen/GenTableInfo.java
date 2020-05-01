package com.iteaj.framework.gen;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * create time: 2020/4/28
 *
 * @author iteaj
 * @since 1.0
 */
public class GenTableInfo extends TableInfo {

    private String prefix;
    private String viewType;
    private List<TableField> allList;

    public String getPrefix() {
        return prefix;
    }

    public GenTableInfo setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public String getOriSql() {
        List<TableField> tableFieldList = getAllList();

        int i=0, num = 10; // 5个字段换行
        StringBuilder sb = new StringBuilder();
        for(TableField item : tableFieldList) {
            i++;
            sb.append(" a.").append(item.getName()).append(',');

            if(i == num) sb.append("\r\t\t");
            else if(i%num==0) sb.append("\r\t\t");
        }

        return sb.substring(0, sb.length()-1);
    }

    public String getOriJoin() {
        if(StringUtils.isBlank(getPrefix())) return "";

        List<TableField> tableFieldList = getAllList();

        int i=0, num = 6; // 5个字段换行
        StringBuilder sb = new StringBuilder();
        for(TableField item : tableFieldList) {
            i++;
            sb.append(' ').append(getPrefix()).append('.').append(item.getName())
                    .append(' ').append(getPrefix()).append('_')
                    .append(item.getName()).append(',');

            if(i == num) sb.append("\r\t\t");
            else if(i%num==0) sb.append("\r\t\t");
        }

        return sb.substring(0, sb.length()-1);
    }

    public int getColumn() {
        List<TableField> tableFieldList = getAllList();

        if(tableFieldList.size()>15) {
            return 4;
        } else if(tableFieldList.size()>8) {
            return 3;
        } else {
            return 1;
        }
    }

    private List<TableField> getAllList() {
        if(allList == null) {
            allList = new ArrayList<>();
            List<TableField> commonFields = getCommonFields();
            if(null != commonFields) allList.addAll(commonFields);
            List<TableField> fields = getFields();
            if(null != fields) allList.addAll(fields);
        }

        return allList;
    }

    public String getViewType() {
        return viewType;
    }

    public GenTableInfo setViewType(String viewType) {
        this.viewType = viewType;
        return this;
    }
}
