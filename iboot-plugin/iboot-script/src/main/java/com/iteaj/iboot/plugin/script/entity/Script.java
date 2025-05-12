package com.iteaj.iboot.plugin.script.entity;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.mybatis.handler.FastjsonTypeHandler;
import com.iteaj.framework.script.ScriptComplete;
import com.iteaj.framework.script.FunctionScript;
import com.iteaj.framework.script.ScriptConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
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
    @TableField(condition = SqlCondition.LIKE)
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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @JSONField(serialize = false)
    @TableField(exist = false)
    private List<ScriptConfig> configs;

    /**
     * 脚本提示内容
     */
    @TableField(exist = false)
    private List<ScriptComplete> completes;

    public static final String TYPE_FILTER = "filter";
    public static final String TYPE_LINKAGE = "linkage";
    public static final String TYPE_MODEL_API = "model_api";
    public static final String TYPE_MODEL_ATTR = "model_attr";
    // 其他地方有引用 {@code ProtocolInvokeUtil#buildDownConfig}
    public static final String KEY_PREFIX = "script:";
    public Script setScript(JSONArray script) {
        this.script = script;
        if(script != null) {
            this.configs = script.toJavaList(ScriptConfig.class);
        }

        return this;
    }

    @Override
    public String getKey() {
        return KEY_PREFIX + getId();
    }
}
