package com.iteaj.iboot.plugin.edge.core.network;

import com.iteaj.iot.message.DefaultMessageHead;

/**
 * 边缘消息头
 * 消息格式:
 * 消息头
 *   1. 1字节: 魔数 0x58
 *   2. 2字节: 消息报文长度(不包含魔数和长度字段)
 *   3. 1字节: 消息类型 {@link EdgeProtocolType}
 *   4. 1字节: 报文方向 (值：1. 请求 2. 应答)
 *   5. 1字节: 预留字节
 *   6. 4字节: 消息id
 * 消息体内容
 *   7. n字节: 数据
 */
public class EdgeMessageHead extends DefaultMessageHead {

    public static final byte MAGIC_NUM = 0x58;

    public EdgeMessageHead(byte[] message) {
        super(message);
    }

    public EdgeMessageHead(EdgeProtocolType type, String msgId, byte[] message) {
        this(null, type, msgId, message);
    }

    public EdgeMessageHead(String equipCode, EdgeProtocolType type, String msgId, byte[] message) {
        super(equipCode, msgId, type, message);
    }
}
