package com.iteaj.plguin.protocol.hj212.network;

import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.message.DefaultMessageHead;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HJ212MessageHead extends DefaultMessageHead {

    /**
     * 系统编码
     */
    private HJ212SysCode sysCode;

    public HJ212MessageHead(byte[] message) {
        super(message);
    }

    public HJ212MessageHead(String equipCode, HJ212SysCode sysCode, ProtocolType type) {
        super(equipCode, type);
        this.sysCode = sysCode;
    }

    @Override
    public HJ212CmdCode getType() {
        return (HJ212CmdCode) super.getType();
    }
}
