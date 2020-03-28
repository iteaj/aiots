package com.iteaj.framework.authority.tenant;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.core.parser.ISqlParserFilter;
import com.baomidou.mybatisplus.core.parser.SqlParserHelper;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import com.iteaj.framework.BaseEntity;
import net.sf.jsqlparser.expression.Expression;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMap;
import org.apache.ibatis.reflection.MetaObject;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * create time: 2020/3/6
 *
 * @author iteaj
 * @since 1.0
 */
public class TenantParseFilter implements TenantHandler, ISqlParserFilter {

    private List<ISqlParser> sqlParserList = new ArrayList<>();
    private static ThreadLocal<Tenant> threadLocal = new ThreadLocal<>();

    public TenantParseFilter() {
        sqlParserList.add(new TenantSqlParser().setTenantHandler(this));
    }

    @Override
    public Expression getTenantId(boolean where) {
        return threadLocal.get().getExpression();
    }

    @Override
    public String getTenantIdColumn() {
        return threadLocal.get().getTenantField();
    }

    @Override
    public boolean doTableFilter(String tableName) {
        return false;
    }

    @Override
    public boolean doFilter(MetaObject metaObject) {
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        Object parameterObject = boundSql.getParameterObject();
        if(parameterObject != null) {
            if(parameterObject instanceof Map) {
                Object ew = ((Map) parameterObject).get("ew");
                if(ew instanceof QueryWrapper) {
                    Object entity = ((QueryWrapper) ew).getEntity();
                    if(null != entity) {
                        BaseEntity baseEntity = (BaseEntity) entity;
                        if(baseEntity == null || null == baseEntity.getTenant()) return true;

                        threadLocal.set(baseEntity.getTenant());
                        return false;
                    }
                }
            }
        }

        return true;
    }

    protected static void removeTenant() {
        threadLocal.remove();
    }

    public ISqlParserFilter getSqlParserFilter() {
        return this;
    }

    public List<ISqlParser> getSqlParserList() {
        return sqlParserList;
    }
}
