package com.iteaj.iboot.plugin.protocol.dlt645.network;

import cn.hutool.core.util.StrUtil;
import com.iteaj.iot.Message;
import com.iteaj.iot.utils.ByteUtil;
import lombok.Getter;

public class Dlt645DtuMessageBody implements Message.MessageBody {

    @Getter
    private String data;
    private byte[] message;
    private final static String suffix = "0016";

    /**
     * @param data 数据标识
     */
    public Dlt645DtuMessageBody(String data) {
        this.data = resolverDate(data);
    }

    public byte[] build(byte[] headMsg) {
        byte[] result = new byte[headMsg.length + this.getLength()];
        System.arraycopy(headMsg, 0, result, 0, headMsg.length);
        System.arraycopy(this.getMessage(), 0, result, headMsg.length, getLength());
        int sum = 0;
        for (int i = 0; i < result.length - 2; i++) {
            sum += result[i];
        }

        // 计算校验码
        result[result.length - 2] = (byte) (sum % 256);
        return result;
    }

    public Dlt645DtuMessageBody read() {
        return this.read(null, null);
    }

    protected Dlt645DtuMessageBody read(Byte blocks) {
        return this.read(blocks, null);
    }

    protected Dlt645DtuMessageBody read(Byte blocks, String date) {
        String strMsg;
        if(blocks != null && StrUtil.isNotBlank(date)) {
            strMsg = "0A" + resolverData() + ByteUtil.bytesToHex(new byte[]{(byte) (blocks + 0x33)}) + resolverDate(date) + suffix;
        } else if(blocks != null) {
            strMsg = "05" + resolverData() + ByteUtil.bytesToHex(new byte[]{(byte) (blocks + 0x33)}) + suffix;
        } else {
            strMsg = "04" + resolverData() + suffix;
        }

        this.message = ByteUtil.hexToByte(strMsg);
        return this;
    }

    private String resolverDate(String date) {
        return date;
    }

    private String resolverData() {
        if(StrUtil.isBlank(this.getData())) {
            throw new NullPointerException("数据标识必填");
        }

        if(this.getData().length() != 8) {
            throw new IllegalArgumentException("数据标识长度必须等于8");
        }

        byte[] result = new byte[4];
        byte[] bytes = ByteUtil.hexToByte(this.getData());
        result[0] = (byte) (bytes[3] + 0x33);
        result[1] = (byte) (bytes[2] + 0x33);
        result[2] = (byte) (bytes[1] + 0x33);
        result[3] = (byte) (bytes[0] + 0x33);
        return ByteUtil.bytesToHex(result);
    }

    @Override
    public byte[] getMessage() {
        return this.message;
    }
}
