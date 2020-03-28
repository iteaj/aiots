package com.iteaj.framework;

import com.iteaj.framework.authority.TreeResource;

/**
 * 可以访问的
 */
public interface UrlResource extends TreeResource {

    String getUrl();

    void setUrl(String url);
}
