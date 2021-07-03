package com.iteaj.framework.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * create time: 2021/7/3
 *
 * @author iteaj
 * @since 1.0
 */
@Data
@ConfigurationProperties(prefix = "cache.shiro")
public class ShiroCacheProperties {


}
