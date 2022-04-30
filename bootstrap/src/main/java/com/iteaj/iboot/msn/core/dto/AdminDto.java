package com.iteaj.iboot.msn.core.dto;

import com.iteaj.iboot.msn.core.entity.Admin;
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
     * 角色列表
     */
    private List<Long> roleIds;

}
