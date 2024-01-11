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
