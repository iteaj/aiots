package com.iteaj.framework.authority.tenant;

import net.sf.jsqlparser.expression.Expression;
import org.apache.ibatis.mapping.StatementType;

import java.io.Serializable;

/**
 * create time: 2020/3/6
 *  多住户实体
 * @author iteaj
 * @since 1.0
 */
public interface Tenant extends Serializable {

    /**
     * 租户字段, 必须为表字段
     * @return
     */
    String getTenantField();

    /**
     * 租户值
     * @return
     */
    Serializable getTenantValue();

    /**
     * sql帧表达式
     * @return
     */
    Expression getExpression();
}
