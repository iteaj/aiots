package com.iteaj.framework.spi.admin;

import com.iteaj.framework.consts.PermStatus;

/**
 * create time: 2020/6/14
 *  菜單源
 * @author iteaj
 * @since 1.0
 */
public interface MenuResource extends UrlResource {

    String getIcon();

    Object getType();

    Object getPosition();

    Object getPermType();

    PermStatus getStatus();
}
