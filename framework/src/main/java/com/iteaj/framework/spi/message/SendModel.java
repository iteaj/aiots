package com.iteaj.framework.spi.message;

import lombok.Data;

@Data
public class SendModel {

    /**
     * 接收人(手机号、邮箱、openid)
     * 如果批量使用逗号隔开
     */
    private String accept;

    /**
     * 发送标题
     */
    private String title;

    /**
     * 发送内容
     */
    private Object content;

    /**
     * 扩展字段
     */
    private Object extra;

    /**
     * 用于发送短信模板
     * @see #templateName
     */
    private String templateId;

    /**
     * 模板名称
     * @see #templateId
     */
    private String templateName;

}
