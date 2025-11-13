package com.iteaj.iboot.module.iot.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.mybatis.handler.FastjsonTypeHandler;
import com.iteaj.framework.validate.AllGroup;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 设备管理
 *
 * @author iteaj
 * @since 2022-05-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_device")
public class Device extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 设备ip
     */
    private String ip;

    /**
     * 父网关设备
     * @see com.iteaj.iboot.module.iot.consts.DeviceType#Child
     * @see com.iteaj.iboot.module.iot.consts.DeviceType#Gateway
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long pid;

    /**
     * 设备uid
     */
    private String uid;

    /**
     * 设备名称
     */
    @NotBlank(message = "设备名称必填", groups = AllGroup.class)
    private String name;

    /**
     * 设备端口
     */
    private Integer port;

    /**
     * 拓展字段
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject extend;

    /**
     * 所属产品
     */
    @NotNull(message = "所属产品必填", groups = AllGroup.class)
    private Long productId;

    /**
     * 所属组
     */
    private Long deviceGroupId;

    /**
     * 设备类型
     */
    @TableField(exist = false)
    private Long productTypeId;

    /**
     * 经度
     */
    @TableField(updateStrategy =  FieldStrategy.IGNORED)
    private String lon;

    /**
     * 纬度
     */
    @TableField(updateStrategy =  FieldStrategy.IGNORED)
    private String lat;

    /**
     * 地址
     */
    @TableField(updateStrategy =  FieldStrategy.IGNORED)
    private String address;

    /**
     * 设备账号
     */
    private String account;

    /**
     * 设备密码
     */
    private String password;

    /**
     * 设备状态
     */
    private DeviceStatus status;

    /**
     * 设备编号
     */
    @NotBlank(message = "设备编号必填", groups = AllGroup.class)
    private String deviceSn;

    /**
     * 父设备编号
     */
    @TableField(exist = false)
    private String parentDeviceSn;

    /**
     * 设备配置
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject config;

    /**
     * 状态切换时间
     */
    private Date switchTime;

    /**
     * 新增时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    public Device build() {
        if(this.extend == null) {
            this.extend = new JSONObject();
            this.extend.put("alias", "");
            this.extend.put("tags", new ArrayList<>());
        }

        return this;
    }

    public Device build(String key, Object value) {
        this.build();
        this.extend.put(key, value);
        return this;
    }

    public Device build(JSONObject extend) {
        this.build();

        if(extend != null) {
            this.extend.putAll(extend);
        }

        return this;
    }

    public Device setAlias(String alias) {
        this.build("alias", alias);
        return this;
    }

    public Device addTag(String tag) {
        this.build();
        this.extend.getJSONArray("tags").add(tag);
        return this;
    }


    public Device addTags(List<String> tags) {
        this.build();
        this.extend.getJSONArray("tags").addAll(tags);
        return this;
    }
}
