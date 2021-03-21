package com.iteaj.framework;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

/**
 * 树结构的实体类.有父子之分的
 */
@Data
@Accessors(chain = true)
public class TreeEntity extends BaseEntity {

    /**当前树节点的父节点*/
    private Long pid;

    /**
     * 此节点的子节点
     */
    @TableField(exist = false)
    private Collection children;

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public TreeEntity addChild(TreeEntity entity) {
        if(CollectionUtils.isEmpty(this.children)) {
            this.children = new ArrayList<>();
        }

        this.children.add(entity);
        return this;
    }

    public Collection getChildren() {
        return children;
    }

    public void setChildren(Collection children) {
        this.children = children;
    }
}
