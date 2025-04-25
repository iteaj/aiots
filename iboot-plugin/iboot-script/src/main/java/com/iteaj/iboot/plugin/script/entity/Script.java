package com.iteaj.iboot.plugin.script.entity;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.mybatis.handler.FastjsonTypeHandler;
import com.iteaj.framework.script.ScriptComplete;
import com.iteaj.framework.script.FunctionScript;
import com.iteaj.framework.script.ScriptConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

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
public class Script extends BaseEntity implements FunctionScript {

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
     * 使用语言
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String language;

    /**
     * 脚本
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONArray script;

    /**
     * 备注
     */
    private String remark;

    @JsonIgnore
    @TableField(exist = false)
    private List<ScriptConfig> configs;

    /**
     * 脚本提示内容
     */
    @TableField(exist = false)
    private List<ScriptComplete> completes;

    public static final String TYPE_FILTER = "filter";
    public static final String TYPE_MODEL_API = "model_api";
    public static final String TYPE_MODEL_ATTR = "model_attr";

    public Script setScript(JSONArray script) {
        this.script = script;
        if(script != null) {
            this.configs = script.toJavaList(ScriptConfig.class);
        }

        return this;
    }
}
