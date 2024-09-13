package com.iteaj.iboot.plugin.protocol.dlt645.protocol;

import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.message.DefaultMessageHead;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class Dlt645MessageHead extends DefaultMessageHead {

    /**
     * 数据标识
     */
    private String address;

    /**
     * 数据块数
     */
    private Byte blocks;

    /**
     * 读取的时间
     */
    private Date date;

    public Dlt645MessageHead(String equipCode, ProtocolType type, String address) {
        this(equipCode, type, address, null);
    }

    public Dlt645MessageHead(String equipCode, ProtocolType type, String address, Byte blocks) {
        this(equipCode, type, address, blocks, null);
    }

    public Dlt645MessageHead(String equipCode, ProtocolType type, String address, Byte blocks, Date date) {
        super(equipCode, type);
        this.address = address;
        this.blocks = blocks;
        this.date = date;
    }

    public Dlt645MessageHead(String equipCode, Dlt645ProtocolType type, byte[] message) {
        super(equipCode, type, message);
    }

    @Override
    public Dlt645ProtocolType getType() {
        return (Dlt645ProtocolType) super.getType();
    }

}
