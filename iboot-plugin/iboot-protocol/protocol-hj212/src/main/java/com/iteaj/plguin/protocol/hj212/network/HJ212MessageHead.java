package com.iteaj.plguin.protocol.hj212.network;

import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.message.DefaultMessageHead;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HJ212MessageHead extends DefaultMessageHead {

    private String QN;
    private HJ212SysCode ST;
    private HJ212CmdCode CN;
    private String PW;
    private String MN;
    private String Flag;

    public HJ212MessageHead(byte[] message) {
        super(message);
    }

    public HJ212MessageHead(String MN, HJ212SysCode ST, HJ212CmdCode CN, String QN, String PW, String Flag) {
        super(MN, String.format("%s:%s", MN, QN), CN);
        this.ST = ST;
        this.CN = CN;
        this.PW = PW;
        this.MN = MN;
        this.QN = QN;
        this.Flag = Flag;
    }

    @Override
    public HJ212CmdCode getType() {
        return (HJ212CmdCode) super.getType();
    }
}
