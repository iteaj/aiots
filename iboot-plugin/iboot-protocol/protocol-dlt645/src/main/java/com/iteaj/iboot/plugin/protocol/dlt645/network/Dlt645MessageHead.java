package com.iteaj.iboot.plugin.protocol.dlt645.network;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.iteaj.iot.FrameworkException;
import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.message.DefaultMessageHead;
import com.iteaj.iot.utils.ByteUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Setter
@Getter
public class Dlt645MessageHead extends DefaultMessageHead {

    /**
     * 数据域
     */
    private String address;

    /**
     * 使用前导码
     */
    private int prefix;

    /**
     * 帧标识符
     */
    private final static byte ID = 0x68;

    public Dlt645MessageHead(String equipCode) {
        super(equipCode, null);
    }

    public Dlt645MessageHead(String equipCode, String address, ProtocolType type) {
        this(equipCode, address, type, 0);
    }

    public Dlt645MessageHead(String equipCode, String address, ProtocolType type, int prefix) {
        super(equipCode, type);
        this.prefix = prefix;
        this.address = address;
    }

    /**
     * 构建报文头
     * @return
     */
    public byte[] build() {
        byte[] message;
         byte[] addressMsg = getAddressMsg();
        if(prefix > 0) {
            if(prefix == 1) {
                message = new byte[] {
                        (byte) 0xFE // 前缀
                        , ID // 帧标识符
                        , 0, 0, 0, 0, 0, 0 // 地址
                        , ID // 帧标识符
                        , getType().getCode() // 控制码
                };

                System.arraycopy(addressMsg, 0, message, 2, 6);
            } else if(prefix == 2) {
                message = new byte[] {
                        (byte) 0xFE, (byte) 0xFE// 前缀
                        , ID // 帧标识符
                        , 0, 0, 0, 0, 0, 0 // 地址
                        , ID // 帧标识符
                        , getType().getCode() // 控制码
                };

                System.arraycopy(addressMsg, 0, message, 3, 6);
            } else if(prefix == 3) {
                message = new byte[] {
                        (byte) 0xFE, (byte) 0xFE, (byte) 0xFE // 前缀
                        , ID // 帧标识符
                        , 0, 0, 0, 0, 0, 0 // 地址
                        , ID // 帧标识符
                        , getType().getCode() // 控制码
                };

                System.arraycopy(addressMsg, 0, message, 4, 6);
            } else if(prefix == 4) {
                message = new byte[] {
                        (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE // 前缀
                        , ID // 帧标识符
                        , 0, 0, 0, 0, 0, 0 // 地址
                        , ID // 帧标识符
                        , getType().getCode() // 控制码
                };

                System.arraycopy(addressMsg, 0, message, 5, 6);
            } else {
                throw new FrameworkException("不支持的前导码个数["+prefix+"]");
            }
        } else {
            message = new byte[] {
                ID // 帧标识符
                , 0, 0, 0, 0, 0, 0 // 地址
                , ID // 帧标识符
                , getType().getCode() // 控制码
            };

            System.arraycopy(addressMsg, 0, message, 1, 6);
        }

        this.setMessage(message);
        return message;
    }

    @Nullable
    private byte[] getAddressMsg() {
        if(StrUtil.isBlank(this.address)) {
            throw new NullPointerException("电表地址不能为空");
        }
        if(this.address.length() > 12) {
            throw new IllegalArgumentException("电表地址长度不能大于12");
        }

        if(this.address.length() < 12) {
            this.address = String.format(this.address, "%012d");
        }

        return ArrayUtil.reverse(ByteUtil.hexToByte(this.address));
    }

    @Override
    public Dlt645ProtocolType getType() {
        return (Dlt645ProtocolType) super.getType();
    }

    public String getAddress() {
        return address;
    }

    public int getPrefix() {
        return prefix;
    }
}
