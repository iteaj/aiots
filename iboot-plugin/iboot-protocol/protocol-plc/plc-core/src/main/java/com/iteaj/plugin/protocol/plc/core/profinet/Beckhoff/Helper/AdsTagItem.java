package com.iteaj.plugin.protocol.plc.core.profinet.Beckhoff.Helper;

/**
 * Ads标签信息
 */
public class AdsTagItem {
    /**
     * 实例化一个默认的对象
     *
     * @param name   标签名
     * @param buffer 缓存的数据对象
     */
    public AdsTagItem(String name, byte[] buffer) {
        this.TagName = name;
        this.Buffer = buffer;
    }

    /**
     * 标签的名称
     */
    public String TagName = "";

    /**
     * 标签的数据缓存信息
     */
    public byte[] Buffer = null;

    /**
     * 绝对地址信息
     */
    public int Location = 0;
}
