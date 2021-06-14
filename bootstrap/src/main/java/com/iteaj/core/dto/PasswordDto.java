package com.iteaj.core.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * create time: 2021/6/8
 *
 * @author iteaj
 * @since 1.0
 */
@Data
@ApiModel("用户密码修改模型")
public class PasswordDto {

    @ApiModelProperty("用户id")
    private Long id;
    @ApiModelProperty("旧密码")
    private String oldPwd;
    @ApiModelProperty("新密码")
    private String password;
}
