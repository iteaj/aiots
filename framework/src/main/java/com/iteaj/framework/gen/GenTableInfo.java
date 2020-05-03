package com.iteaj.framework.gen;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.FileType;
import com.iteaj.util.CommonUtils;
import org.apache.commons.collections.SetUtils;

import java.util.ArrayList;
import java.util.Arrays;
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
    private String oriSql;
    private String insertSql;
    private String oriJoinSql;
    private List<String> writeFlag;
    private String lowerFirstEntity;
    private String serviceFieldName;
    private List<TableField> allList;

    public String getPrefix() {
        return prefix;
    }

    public GenTableInfo setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public GenTableInfo buildOriSql(String[] writeType) {
        if(CommonUtils.isNotEmpty(getAllList())) {
            this.oriSql = handleOriSql();
            this.oriJoinSql = handleOriJoinSql();
            this.insertSql = handleInsertSql();
        }
        if(CommonUtils.isNotEmpty(writeType)) {
            writeFlag = Arrays.asList(writeType);
        }

        this.setComment(this.getComment()!=null ? this.getComment() : "");
        this.lowerFirstEntity = StringUtils.firstCharToLower(getEntityName());
        this.serviceFieldName = StringUtils.firstCharToLower(getServiceName().substring(1));
        return this;
    }

    public String handleOriSql() {
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

    public String handleOriJoinSql() {
        if(CommonUtils.isBlank(getPrefix())) return "";

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

    public String handleInsertSql() {
        List<TableField> tableFieldList = getFields();

        int i=0, num = 7; // 7个字段换行
        StringBuilder sb = new StringBuilder("(");
        for(TableField item : tableFieldList) {
            i++;
            sb.append("#{item.").append(item.getPropertyName()).append("},");

            if(i == num) sb.append("\r\t\t");
            else if(i%num==0) sb.append("\r\t\t\t");
        }
        return sb.replace(sb.length()-1, sb.length(), ")").toString();
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

    public boolean isCreate(FileType fileType) {
        if(this.writeFlag == null) return true;

        return this.writeFlag.contains(fileType.name());
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

    public String getLowerFirstEntity() {
        return lowerFirstEntity;
    }

    public GenTableInfo setLowerFirstEntity(String lowerFirstEntity) {
        this.lowerFirstEntity = lowerFirstEntity;
        return this;
    }

    public String getServiceFieldName() {
        return serviceFieldName;
    }

    public GenTableInfo setServiceFieldName(String serviceFieldName) {
        this.serviceFieldName = serviceFieldName;
        return this;
    }

    public String getViewType() {
        return viewType;
    }

    public GenTableInfo setViewType(String viewType) {
        this.viewType = viewType;
        return this;
    }

    public String getOriSql() {
        return oriSql;
    }

    public GenTableInfo setOriSql(String oriSql) {
        this.oriSql = oriSql;
        return this;
    }

    public String getInsertSql() {
        return insertSql;
    }

    public GenTableInfo setInsertSql(String insertSql) {
        this.insertSql = insertSql;
        return this;
    }

    public String getOriJoinSql() {
        return oriJoinSql;
    }

    public GenTableInfo setOriJoinSql(String oriJoinSql) {
        this.oriJoinSql = oriJoinSql;
        return this;
    }

    public List<String> getWriteFlag() {
        return writeFlag;
    }

    public GenTableInfo setWriteFlag(List<String> writeFlag) {
        this.writeFlag = writeFlag;
        return this;
    }
}
