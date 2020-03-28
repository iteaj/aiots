package com.iteaj.framework.authority.tenant;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.sql.Connection;

/**
 * create time: 2020/3/6
 *
 * @author iteaj
 * @since 1.0
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class TenantPaginationInterceptor extends PaginationInterceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            return super.intercept(invocation);
        } finally {
            TenantParseFilter.removeTenant();
        }
    }
}
