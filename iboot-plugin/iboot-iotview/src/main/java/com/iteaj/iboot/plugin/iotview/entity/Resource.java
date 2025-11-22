package com.iteaj.iboot.plugin.iotview.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

import static com.baomidou.mybatisplus.annotation.IdType.ASSIGN_UUID;

/**
 * <p>
 * 大屏资源
 * </p>
 *
 * @author iteaj
 * @since 2025-07-17
 */
@Data
@Accessors(chain = true)
@TableName("isv_resource")
public class Resource implements Entity<String> {

    private static final long serialVersionUID = 1L;

    @TableId(type = ASSIGN_UUID)
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 路径
     */
    private String url;

    /**
     * 资源类型
     */
    private String type;

    /**
     * 创建时间
     */
    private Date createTime;

    public void setId(String id) {
        this.id = id;
    }

    public static final String BG_IMG = "bg";

}
