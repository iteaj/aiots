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

    public HJ212MessageHead buildResp() {
        HJ212CmdCode cmdCode = isNotifyCmd() ? HJ212CmdCode.通知应答 : HJ212CmdCode.数据应答;
        return new HJ212MessageHead(getMN(), HJ212SysCode.系统交互, cmdCode, getQN(), getPW(), "4");
    }

    public boolean isNotifyCmd() {
        switch (this.CN) { // 通知命令
            case 现场机时间校准请求:
            case 停止察看污染物实时数据:
            case 停止察看设备运行状态:
                return true;
        }

        return false;
    }

    @Override
    public HJ212CmdCode getType() {
        return (HJ212CmdCode) super.getType();
    }
}
