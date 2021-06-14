package com.iteaj.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * create time: 2020/4/22
 *
 * @author iteaj
 * @since 1.0
 */
@Data
@ApiModel
@Accessors(chain = true)
@TableName("t_opera_log")
public class OperaLog extends BaseEntity {

    private String ip;
    private String url;
    private String msn;
    private String type;
    private Long userId;
    @TableField(condition = "%s >= #{%s}")
    private Long millis;
    private String userName;
    private String shortName;
    private String status;
    private String errMsg;
    private String title;

    public OperaLog() { }

    public OperaLog(String type, String userName, String shortName, String status) {
        this.type = type;
        this.userName = userName;
        this.shortName = shortName;
        this.status = status;
    }

}
