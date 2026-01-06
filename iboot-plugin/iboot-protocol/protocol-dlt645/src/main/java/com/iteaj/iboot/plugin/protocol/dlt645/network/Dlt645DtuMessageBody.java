package com.iteaj.iboot.plugin.protocol.dlt645.network;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.iteaj.iboot.plugin.protocol.dlt645.Dlt645Util;
import com.iteaj.iot.Message;
import com.iteaj.iot.utils.ByteUtil;
import lombok.Getter;

import java.util.Date;

public class Dlt645DtuMessageBody implements Message.MessageBody {

    @Getter
    private String data;
    private byte[] message;
    private final static String suffix = "0016";

    public Dlt645DtuMessageBody() {

    }

    public byte[] build(byte[] headMsg, int prefix) {
        byte[] result = new byte[headMsg.length + this.getLength()];
        System.arraycopy(headMsg, 0, result, 0, headMsg.length);
        System.arraycopy(this.getMessage(), 0, result, headMsg.length, getLength());
        int sum = 0;
        for (int i = prefix; i < result.length - 2; i++) {
            sum += result[i];
        }

        // 计算校验码
        result[result.length - 2] = (byte) (sum % 256);
        return result;
    }

    public Dlt645DtuMessageBody read(String data) {
        return this.read(data, null);
    }

    protected Dlt645DtuMessageBody read(String data, Byte blocks) {
        return this.read(data, blocks, null);
    }

    protected Dlt645DtuMessageBody read(String data, Byte blocks, String date) {
        String strMsg;
        if(blocks != null && StrUtil.isNotBlank(date)) {
            strMsg = "0A" + Dlt645Util.resolverData(data) + ByteUtil.bytesToHex(new byte[]{(byte) (blocks + 0x33)}) + Dlt645Util.resolverDate(date) + suffix;
        } else if(blocks != null) {
            strMsg = "05" + Dlt645Util.resolverData(data) + ByteUtil.bytesToHex(new byte[]{(byte) (blocks + 0x33)}) + suffix;
        } else {
            strMsg = "04" + Dlt645Util.resolverData(data) + suffix;
        }

        this.message = ByteUtil.hexToByte(strMsg);
        return this;
    }

    public Dlt645DtuMessageBody mpd() {
        return this;
    }


    /**
     * 电表清零
     * @see Dlt645ProtocolType#rem
     * @return
     */
    public Dlt645DtuMessageBody rem(String perm, String code, String pwd) {
        String strMsg = "08" + perm + Dlt645Util.reverse(pwd) + Dlt645Util.reverse(code) + suffix;
        this.message = ByteUtil.hexToByte(strMsg);
        return this;
    }

    /**
     * 事件清零
     * @param perm
     * @param code
     * @param pwd
     * @param type 总事件清零 FFFFFFFF  分项事件清零 FFXXXXXX
     * @return
     */
    public Dlt645DtuMessageBody rec(String perm, String code, String pwd, String type) {
        String strMsg = "0C" + perm + Dlt645Util.reverse(pwd) + Dlt645Util.reverse(code) + Dlt645Util.reverse(type) + suffix;
        this.message = ByteUtil.hexToByte(strMsg);
        return this;
    }

    /**
     * 最大需量清零
     * @param code 8位操作者代码
     * @param perm 权限
     * @param pwd 6位密码
     * @see Dlt645ProtocolType#rmd
     * @return
     */
    public Dlt645DtuMessageBody rmd(String perm, String code, String pwd) {
        String strMsg = "08" + perm + Dlt645Util.reverse(pwd) + Dlt645Util.reverse(code) + suffix;
        this.message = ByteUtil.hexToByte(strMsg);
        return this;
    }

    /**
     * 广播校时
     * @see Dlt645ProtocolType#rts
     * @return
     */
    public Dlt645DtuMessageBody rts(Date time) {
        String timeStr = DateUtil.format(time, "yyMMddHHmmss");
        String strMsg = "06" + Dlt645Util.resolverDate(timeStr) + suffix;
        this.message = ByteUtil.hexToByte(strMsg);
        return this;
    }

    /**
     * 冻结命令
     * @param timeValue
     * @return
     */
    public Dlt645DtuMessageBody fCmd(Date timeValue) {
        String timeStr = DateUtil.format(timeValue, "MMddHHmm");
        String strMsg = "04" + Dlt645Util.resolverCommonData(timeStr) + suffix;
        this.message = ByteUtil.hexToByte(strMsg);
        return this;
    }

    /**
     * 读电表地址
     * @return
     */
    public Dlt645DtuMessageBody readMA() {
        this.message = ByteUtil.hexToByte("00" + suffix);
        return this;
    }

    /**
     * 修改电表地址
     * @param address
     * @return
     */
    public Dlt645DtuMessageBody writeMA(String address) {
        if(StrUtil.isBlank(address) || address.length() != 12) {
            throw new IllegalArgumentException("电表编号必须12位, 不足12位的前面补零");
        }

        String strMsg = "06" + Dlt645Util.resolverCommonData(address) + suffix;
        this.message = ByteUtil.hexToByte(strMsg);
        return this;
    }

    /**
     * 修改通讯速率
     * @param rate 通讯速率关键字
     * @return
     */
    public Dlt645DtuMessageBody writeMcr(String rate) {
        if(StrUtil.isBlank(rate)) {
            throw new IllegalArgumentException("通讯速率未指定");
        }

        String strMsg = "01" + rate + suffix;
        this.message = ByteUtil.hexToByte(strMsg);
        return this;
    }

    /**
     * 控制设备
     * @param code
     * @param perm
     * @param pwd
     * @param cmd
     * @return
     */
    public Dlt645DtuMessageBody writeCtrl(String code, String perm, String pwd, String cmd, Date time) {
        this.message = new byte[19];
        this.message[0] = 16; // 长度
        String data = perm + Dlt645Util.reverse(pwd) + Dlt645Util.reverse(code) + cmd + "00" + DateUtil.format(time, "ssmmHHddMMyy");
        byte[] bytes = ByteUtil.hexToByte(data);
        for (int i = 0; i < bytes.length; i++) {
            message[i + 1] = (byte) (bytes[i] + 0x33);
        }

        message[18] = 0x16;
        return this;
    }

    /**
     * @param data 数据标识
     * @param value 要写的数据
     * @param code 操作者代码
     * @param perm 权限 (98[密文+mac], 99[明文+mac])
     * @param pwd 密码
     * @return
     */
    public Dlt645DtuMessageBody write(String data, String value, String code, String perm, String pwd) {
        int valueLength = value.length() % 2 == 0 ? value.length() / 2 : (value.length() / 2 + 1);
        final int length = 12 + valueLength;
        this.message = new byte[length + 3];
        this.message[0] = (byte) (length);
        String dataValue = Dlt645Util.reverse(data) + perm + Dlt645Util.reverse(pwd) + Dlt645Util.reverse(code) + Dlt645Util.reverse(value);
        byte[] bytes = ByteUtil.hexToByte(dataValue);
        for (int i = 0; i < bytes.length; i++) {
            message[i + 1] = (byte) (bytes[i] + 0x33);
        }

        message[length + 2] = 0x16;
        return this;
    }

    @Override
    public byte[] getMessage() {
        return this.message;
    }
}
