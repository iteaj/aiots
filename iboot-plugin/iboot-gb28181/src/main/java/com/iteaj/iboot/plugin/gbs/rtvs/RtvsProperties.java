package com.iteaj.iboot.plugin.gbs.rtvs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "media.rtvs")
public class RtvsProperties {

    /**
     * gbsip配置信息
     */
    private GbSip sip;

    /**
     * 流媒体地址
     */
    private String host;

    /**
     * 流媒体端口
     */
    private Integer port;

    @Data
    public static class GbSip {
        /**
         * 登录账号
         */
        private String userName;

        /**
         * 登录密码
         */
        private String password;

        /**
         * 请求地址
         */
        private String apiUri;
    }
}
