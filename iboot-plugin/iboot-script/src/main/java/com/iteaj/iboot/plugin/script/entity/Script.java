package com.iteaj.iboot.plugin.script.entity;

import com.alibaba.fastjson.JSONArray;
import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.spi.iot.listener.filter.EvalScript;
import com.iteaj.framework.spi.iot.listener.filter.ScriptConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 脚本引擎
 * </p>
 *
 * @author iteaj
 * @since 2025-04-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_script")
public class Script extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 脚本类型
     */
    private String type;

    /**
     * 脚本名称
     */
    private String name;

    /**
     * 脚本
     */
    private JSONArray script;

    /**
     * 备注
     */
    private String remark;


}
