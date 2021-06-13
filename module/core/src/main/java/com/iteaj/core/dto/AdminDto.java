package com.iteaj.core.dto;

import com.iteaj.core.entity.Admin;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * create time: 2019/11/27
 *
 * @author iteaj
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class AdminDto extends Admin {

    /**
     * 所属部门
     */
    private String deptName;

    /**
     * 角色名称列表
     */
    private String roleNames;

    /**
     * 是否是超级管理员
     */
    private List<Long> roleIds;

}
