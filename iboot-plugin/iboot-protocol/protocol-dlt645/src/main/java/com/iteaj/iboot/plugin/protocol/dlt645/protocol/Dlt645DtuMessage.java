package com.iteaj.iboot.plugin.protocol.dlt645.protocol;

import com.iteaj.iot.server.dtu.message.DtuServerMessageAbstract;
import com.iteaj.iot.utils.ByteUtil;

public class Dlt645DtuMessage extends DtuServerMessageAbstract {

    /**
     * 标识符
     */
    private final static byte ID = 0x68;

    public Dlt645DtuMessage(byte[] message) {
        super(message);
    }

    public Dlt645DtuMessage(Dlt645MessageHead head, Dlt645DtuMessageBody body) {
        super(head, body);
    }

    @Override
    protected MessageHead doBuild(byte[] message, String equipCode) {
        return null;
    }

    @Override
    public void writeBuild() {
        byte[] message;
        String strMsg = "68" + getHead().getEquipCode() + "68" + getHead().getAddress() + "%s%s";
        StringBuilder builder = new StringBuilder().append("68").append(getHead().getEquipCode()).append("68").append(getHead().getType().getCode());
        Dlt645MessageHead head = getHead();
        byte[] deviceSnData = ByteUtil.hexToByte(head.getEquipCode());
        switch (head.getType()) {
            case readReq:
                builder.append(getHead().getType().getCode());
                byte[] address = ByteUtil.hexToByte(head.getAddress());
                if(head.getBlocks() != null && head.getDate() != null) {
                    message = new byte[] {(byte) 0xFE, (byte) 0XFE, (byte) 0XFE, (byte) 0XFE, ID, 0, 0, 0, 0, ID, head.getType().getCode(), 0x0A, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x16};
                } else if(head.getBlocks() != null) {
                    message = new byte[] {ID, 0, 0, 0, 0, ID, head.getType().getCode(), 0x05, 0, 0, 0, 0, 0, 0, 0x16};
                } else {
                    message = new byte[] {ID, 0, 0, 0, 0, ID, head.getType().getCode(), 0x05, 0, 0, 0, 0, 0, 0x16};
                }

                System.arraycopy(deviceSnData, 0, message, 1, 4);
                System.arraycopy(address, 0, message, 8, 4);
                message[12] = (byte) 0xff;
                break;
            case writeReq:
                break;
            case mcr:
                break;
            case mpd:
                break;
            case rec:
                break;
            case rem:
                break;
            case rmd:
                break;
            case rts:
                break;
            case fCmd:
                break;
            default: throw new IllegalArgumentException("");
        }
    }

    @Override
    public Dlt645MessageHead getHead() {
        return (Dlt645MessageHead) super.getHead();
    }

    @Override
    public Dlt645DtuMessageBody getBody() {
        return (Dlt645DtuMessageBody) super.getBody();
    }
}
