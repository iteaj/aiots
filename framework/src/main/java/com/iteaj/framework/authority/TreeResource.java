package com.iteaj.framework.authority;

import com.iteaj.framework.Resource;

import java.util.Collection;

public interface TreeResource extends Resource {

    /**
     * 子资源列表
     * @return
     */
    Collection getChildren();

    void setChildren(Collection children);
}
